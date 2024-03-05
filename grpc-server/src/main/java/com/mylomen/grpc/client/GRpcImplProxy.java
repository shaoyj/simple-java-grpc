package com.mylomen.grpc.client;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.google.protobuf.ByteString;
import com.mylomen.gprc.client.domain.MynResponse;
import com.mylomen.grpc.constant.CommonConstants;
import com.mylomen.grpc.constant.LogConstants;
import com.mylomen.grpc.pb.BaseServiceGrpc;
import com.mylomen.grpc.pb.ByteResult;
import com.mylomen.grpc.pb.ComReq;
import com.mylomen.grpc.utils.AppContextUtil;
import com.mylomen.grpc.utils.HttpLocalThread;
import com.mylomen.grpc.utils.JsonUtils;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GRpcImplProxy implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(GRpcImplProxy.class);

    private final String serviceName;

    public GRpcImplProxy(String serviceName) {
        this.serviceName = serviceName;
    }


    /**
     * 真正执行的方法,会被aop拦截
     *
     * @param proxy  被代理对象
     * @param method 被代理对象的方法
     * @param args   被代理对象的方法的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MynResponse<Object> mynResponse = new MynResponse<>();
        GRpcClientCore grpcClientCore = AppContextUtil.getApplicationContext().getBean(GRpcClientCore.class);
        Map<String, BaseServiceGrpc.BaseServiceBlockingStub> serverNameStudMap = grpcClientCore.getServerNameStudMap();
        BaseServiceGrpc.BaseServiceBlockingStub stub = serverNameStudMap.get(serviceName);
        if (Objects.isNull(stub)) {
            mynResponse.setCode(-1);
            mynResponse.setMsg("server not found");
            log.error("server_not_found method:{}", method);
            return mynResponse;
        }

        //打印当前使用的线程是虚拟线程还是平台线程
//        System.out.println("client: "+Thread.currentThread());

        //构建请求信息
        ComReq.Builder builder = ComReq.newBuilder()
                //todo 随机数字
                .setId(0L)
                .setTarget(method.getDeclaringClass().getSimpleName().toLowerCase())
                .setMethod(method.getName());

        //支持 空参数
        if (ArrayUtils.isNotEmpty(args)) {
            builder.setBody(ByteString.copyFrom(JSON.toJSONBytes(args[0])));
        }

        //setHeaders
        Map<String, Object> map = parseHeaderMap();
        if (MapUtils.isNotEmpty(map)) {
            builder.setHeaders(ByteString.copyFrom(JSON.toJSONBytes(map)));
        }

        //传入请求 接收返回
        Instant start = Instant.now();
        ByteResult execute;
        try {
            execute = stub
                    //设置超时时间
                    .withDeadlineAfter(CommonConstants.Base.TimeoutSecond, TimeUnit.SECONDS)
                    .execute(builder.build());
        } catch (StatusRuntimeException e) {
            mynResponse.setCode(-1);
            mynResponse.setMsg(e.getMessage());

            //判断是否是 超时
            Status status = e.getStatus();
            if (status.getCode().equals(Status.Code.DEADLINE_EXCEEDED)) {
                mynResponse.setCode(HttpStatus.REQUEST_TIMEOUT.value());
                mynResponse.setMsg("System timeout");
            }
            return mynResponse;
        } catch (Exception e) {
            mynResponse.setCode(-1);
            mynResponse.setMsg(e.getMessage());
            return mynResponse;
        } finally {
            //耗时
            Instant end = Instant.now();
            long cost = start.until(end, ChronoUnit.SECONDS);
            if (cost > LogConstants._COST_MONITOR_TIMEOUT) {
                log.info("{} rpc_client cost_{} biz_{}",
                        LogConstants._COST_MONITOR,
                        cost,
                        builder.getTarget() + "_" + builder.getMethod());
            }
        }


        //only code ==0 is success
        if (execute.getCode() != 0) {
            mynResponse.setCode(execute.getCode());
            mynResponse.setMsg(execute.getMsg());
            return mynResponse;
        }

        //parse data
        mynResponse.setCode(0);
        mynResponse.setMsg(execute.getMsg());

        //方法返回参数集合
        ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
        ResolvableType generic = resolvableType.getGenerics()[0];
        Object o = JsonUtils.parseObject(execute.getData().toByteArray(), generic.getType());
        mynResponse.setData(o);
        return mynResponse;
    }


    private Map<String, Object> parseHeaderMap() {
        HttpServletRequest request = HttpLocalThread.getRequest();
        if (Objects.isNull(request)) {
            return Collections.emptyMap();
        }

        Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
        //traceId
        String traceId = HttpLocalThread.GetHeaderValue(CommonConstants.Header.TRACE_ID);
        if (!ObjectUtils.isEmpty(traceId)) {
            map.put(CommonConstants.Header.TRACE_ID, traceId);
        }
        return map;
    }

}
