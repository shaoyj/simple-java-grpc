package com.mylomen.grpc.server;


import com.mylomen.grpc.utils.AppContextUtil;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GRpcServerCore implements ApplicationListener<ContextClosedEvent>, InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(GRpcServerCore.class);

    private Server server = null;

    public void init() {

        MynServerGRpcImplProxy serverGRpcProxy = AppContextUtil.getApplicationContext().getBean(MynServerGRpcImplProxy.class);

        try {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors() * 2 + 1,
                    Runtime.getRuntime().availableProcessors() * 5,
                    10, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(512),
                    //factory
                    (Runnable r) -> {
                        //创建一个线程
                        Thread t = new Thread(r);
                        //给创建的线程设置UncaughtExceptionHandler对象 里面实现异常的默认逻辑
                        Thread.setDefaultUncaughtExceptionHandler((Thread thread1, Throwable e) -> logger.error("rpcServerThreadPoolTaskExecutor_catch_err :{}", e.getMessage()));
                        return t;
                    },
                    //拒绝策略
                    (Runnable r, ThreadPoolExecutor executorInner) -> {
                        logger.error("rpcServerThreadPoolTaskExecutor_custom_Policy :{}  from :{}", r.toString(), executorInner);
                        if (!executorInner.isShutdown()) {
                            r.run();
                        }
                    });

            //这里面还可以加入拦截器,过滤器,超时等
            server = ServerBuilder.forPort(MynServerConfigurationSelector.getPort())
                    .keepAliveTime(60, TimeUnit.SECONDS)
                    .keepAliveTimeout(30, TimeUnit.SECONDS)
                    .permitKeepAliveTime(30, TimeUnit.SECONDS)
                    .permitKeepAliveWithoutCalls(true)
                    .executor(executor)
                    .addService(serverGRpcProxy)
                    .build()
                    .start();


        } catch (Exception e) {
            logger.error("服务端出错 e", e);
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (server != null) {
            server.shutdown();
        }
    }

    @Override
    public void afterPropertiesSet() {
        this.init();
    }
}
