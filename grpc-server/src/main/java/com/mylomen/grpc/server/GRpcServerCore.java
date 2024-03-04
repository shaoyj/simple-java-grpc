package com.mylomen.grpc.server;


import com.mylomen.grpc.utils.AppContextUtil;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GRpcServerCore implements ApplicationListener<ContextClosedEvent>, InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(GRpcServerCore.class);

    private Server server = null;

    public void init() {

        MynServerGRpcImplProxy serverGRpcProxy = AppContextUtil.getApplicationContext().getBean(MynServerGRpcImplProxy.class);

        try {
            //这里面还可以加入拦截器,过滤器,超时等
            server = ServerBuilder.forPort(MynServerConfigurationSelector.getPort())
                    .keepAliveTime(60, TimeUnit.SECONDS)
                    .keepAliveTimeout(30, TimeUnit.SECONDS)
                    .permitKeepAliveTime(30, TimeUnit.SECONDS)
                    .permitKeepAliveWithoutCalls(true)
                    //使用虚拟线程
                    .executor(Executors.newVirtualThreadPerTaskExecutor())
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
