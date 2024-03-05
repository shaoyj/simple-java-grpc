package com.mylomen.grpc.server;

import com.alibaba.fastjson2.JSON;
import com.google.protobuf.ByteString;
import com.mylomen.gprc.client.domain.MynHeadersReq;
import com.mylomen.gprc.client.domain.MynResponse;
import com.mylomen.grpc.pb.BaseServiceGrpc;
import com.mylomen.grpc.pb.ByteResult;
import com.mylomen.grpc.pb.ComReq;
import com.mylomen.grpc.utils.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class MynServerGRpcImplProxy extends BaseServiceGrpc.BaseServiceImplBase implements InitializingBean, ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(MynServerGRpcImplProxy.class);

    /**
     * <interfaceName,<group_methodName,method>
     */
    private Map<String, Map<String, Method>> interfaceMethodRpcMap;


    /**
     * <interfaceName,interfaceClass>
     */
    private Map<String, Class<?>> interfaceBeanRpcMap;

    /**
     * rpc 服务提供者 后缀
     */
    private final static String rpc_service_provider_suffix = "Rpc";


    @Override
    public void execute(ComReq request, StreamObserver<ByteResult> responseObserver) {
        String target = request.getTarget().toLowerCase();
        Object proxyObject = getProxyObjectByInterfaceName(target);
        if (Objects.isNull(proxyObject)) {
            responseObserver.onNext(ByteResult.newBuilder().setCode(-1).setMsg("proxyObject is not supported").build());
            responseObserver.onCompleted();
            return;
        }
        Method proxyMethod = getProxyMethodByInterfaceName(target, request.getMethod());
        if (Objects.isNull(proxyMethod)) {
            responseObserver.onNext(ByteResult.newBuilder().setCode(-1).setMsg("proxyMethod is not supported").build());
            responseObserver.onCompleted();
            return;
        }

        //打印当前使用的线程是虚拟线程还是平台线程
//        System.out.println("server: "+Thread.currentThread());

        //解析参数 todo
        Parameter[] parameters = proxyMethod.getParameters();
        List<Object> parameterList = new ArrayList<>(parameters.length);
        for (Parameter parameterType : parameters) {
            Object parse = JsonUtils.parseObject(request.getBody().toByteArray(), parameterType.getParameterizedType());
            //支持 FbHeadersReq 对象 自动化组装 header信息
            if (parameterType.getParameterizedType().equals(MynHeadersReq.class) || parse instanceof MynHeadersReq) {
                MynHeadersReq tempReq = (MynHeadersReq) parse;
                //兼容get 请求 body 为空的场景
                if (Objects.isNull(tempReq)) {
                    tempReq = new MynHeadersReq();
                }

                //解析& fill -> header
                Map<String, Object> headerMap = JsonUtils.parseMap(request.getHeaders().toByteArray());
                MynHeadersReqUtils.ParseHeadersInfo(tempReq, headerMap);
                MynHeadersReqUtils.fillLog(tempReq);
                MynRpcContent.setValue(headerMap);

                parameterList.add(tempReq);
            } else {
                parameterList.add(parse);
            }
        }

        // 执行方法
        Object invoke;
        try {
            invoke = proxyMethod.invoke(proxyObject, parameterList.toArray());
        } catch (Exception e) {
            //if the underlying method throws an exception。 and getTargetException==IllegalArgumentException then parse errMsg
            if (e instanceof InvocationTargetException invE) {
                Throwable targetException = invE.getTargetException();
                if (targetException instanceof IllegalArgumentException ill) {
                    responseObserver.onNext(ByteResult.newBuilder().setCode(-1).setMsg(ill.getMessage()).build());
                    responseObserver.onCompleted();
                    logger.warn("bizErr parameterList:{} proxyObject:{} proxyMethod:{} e", parameterList, proxyObject.getClass(), proxyMethod.getName(), ill);
                    return;
                }
            }

            responseObserver.onNext(ByteResult.newBuilder().setCode(-1).setMsg("remote rpc err").build());
            responseObserver.onCompleted();
            logger.error("remote_rpc_err parameterList:{} proxyObject:{} proxyMethod:{} e", parameterList, proxyObject.getClass(), proxyMethod.getName(), e);
            //todo 业务告警
            return;
        } finally {
            MynRpcContent.clear();
            MDC.clear();
        }

        //null
        if (invoke == null) {
            responseObserver.onNext(ByteResult.newBuilder().setCode(-1).setMsg("result is null").build());
            responseObserver.onCompleted();
            return;
        }

        //ByteResult
        if (invoke instanceof ByteResult) {
            responseObserver.onNext((ByteResult) invoke);
            responseObserver.onCompleted();
            return;
        }

        if (invoke instanceof MynResponse<?> result) {
            ByteResult.Builder resBuilder = ByteResult.newBuilder().setCode(result.getCode());
            if (Objects.nonNull(result.getMsg())) {
                resBuilder.setMsg(result.getMsg());
            }
            if (Objects.nonNull(result.getData())) {
                resBuilder.setData(ByteString.copyFrom(JSON.toJSONBytes(result.getData())));
            }

            responseObserver.onNext(resBuilder.build());
            responseObserver.onCompleted();
            return;
        }

        //默认返回
        responseObserver.onNext(ByteResult.newBuilder().setCode(-1).setMsg("The returned type is not supported").build());
        responseObserver.onCompleted();
    }


    private void initInterfaceBeanMethodMap() {
        //扫描到的目标类集合
        Set<Class<?>> targetClazzSet = MynClazzUtils.loadClazzSet(MynServerConfigurationSelector.getPkgList(), MynGRpcServer.class);
        if (CollectionUtils.isEmpty(targetClazzSet)) {
            throw new RuntimeException("loadClazzSet_empty : " + Arrays.toString(MynServerConfigurationSelector.getPkgList()));
        }

        //<interfaceName,<group_methodName,method>
        Map<String, Map<String, Method>> interfaceMethodRpcMap = new ConcurrentHashMap<>(targetClazzSet.size() * 2);
        //<interfaceName,interfaceClazz>
        Map<String, Class<?>> interfaceBeanRpcNameMap = new ConcurrentHashMap<>(targetClazzSet.size() * 2);

        //遍历 fbServer
        for (Class<?> clazz : targetClazzSet) {
            Class<?> rpcInterface = findRpcInterface(clazz);
            if (Objects.isNull(rpcInterface)) {
                throw new RuntimeException("Initialize the current class exception, please check. at " + clazz);
            }

            //parse interfaceMethodList
            List<Method> classMethodList = Arrays.stream(rpcInterface.getDeclaredMethods()).toList();
            if (CollectionUtils.isEmpty(classMethodList)) {
                continue;
            }

            //rpcInterfaceName
            String rpcInterfaceName = rpcInterface.getSimpleName().toLowerCase();
            //rpcInterfaceMethodMap <methodName,Method>
            Map<String, Method> rpcInterfaceMethodMap = interfaceMethodRpcMap.computeIfAbsent(rpcInterfaceName, k -> new ConcurrentHashMap<>(classMethodList.size() * 2));
            //put rpcInterfaceName,rpcInterfaceClass
            interfaceBeanRpcNameMap.put(rpcInterfaceName, rpcInterface);
            //遍历某一个 fbServer 的所有method
            for (Method method : classMethodList) {
                String methodName = method.getName();
                if (rpcInterfaceMethodMap.containsKey(methodName)) {
                    throw new RuntimeException("Method overloading is not supported for now. at " + clazz);
                }

                //返回类型 验证
                Class<?> returnType = method.getReturnType();
                if (returnType != MynResponse.class && returnType != ByteResult.class) {
                    throw new RuntimeException("The returned type is not supported at " + clazz);
                }

                rpcInterfaceMethodMap.put(methodName, method);
            }
        }


        this.interfaceMethodRpcMap = interfaceMethodRpcMap;
        this.interfaceBeanRpcMap = interfaceBeanRpcNameMap;
    }


    /**
     * 搜索到 以rpc 结尾的接口
     *
     * @param aClass 类型
     * @return 返回搜索到的类
     */
    protected Class<?> findRpcInterface(Class<?> aClass) {
        if (Objects.isNull(aClass)) {
            return null;
        }

        Class<?> finalInterface = null;
        Class<?>[] interfaces = aClass.getInterfaces();
        for (Class<?> aInterface : interfaces) {
            String aInterfaceName = aInterface.getSimpleName();
            if (!aInterfaceName.endsWith(rpc_service_provider_suffix)) {
                continue;
            }

            //check multiply
            if (Objects.isNull(finalInterface)) {
                finalInterface = aInterface;
            } else {
                throw new RuntimeException("Check if multiple rpc interfaces are implemented. at " + aClass);
            }
        }

        return finalInterface;
    }


    private Object getProxyObjectByInterfaceName(String interfaceName) {
        Class<?> aClass = interfaceBeanRpcMap.get(interfaceName.toLowerCase());
        if (Objects.isNull(aClass)) {
            return null;
        }
        return AppContextUtil.getBeanAuto(aClass);
    }


    private Method getProxyMethodByInterfaceName(String interfaceName, String methodName) {
        Map<String, Method> groupMethodMap = interfaceMethodRpcMap.get(interfaceName);
        if (CollectionUtils.isEmpty(groupMethodMap)) {
            return null;
        }

        return groupMethodMap.get(methodName);
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        if (AppContextUtil.getApplicationContext() == null) AppContextUtil.setApplicationContext(ctx);
    }


    @Override
    public void afterPropertiesSet() {
        initInterfaceBeanMethodMap();
    }


}
