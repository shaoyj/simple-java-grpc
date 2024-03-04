package com.mylomen.grpc.client;


import com.mylomen.grpc.pb.BaseServiceGrpc;
import com.mylomen.grpc.utils.AppContextUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class GRpcClientCore implements ApplicationListener<ContextClosedEvent>, InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(GRpcClientCore.class);


    private Map<String, ManagedChannel> channelMap = null;
    @Getter
    private Map<String, BaseServiceGrpc.BaseServiceBlockingStub> serverNameStudMap = null;

    public void run() {
        MynGRpcClientConfig rpcConfig = AppContextUtil.getApplicationContext().getBean(MynGRpcClientConfig.class);
        if (CollectionUtils.isEmpty(rpcConfig.getMap())) {
            throw new RuntimeException("please check myn-grpc");
        }

        channelMap = new HashMap<>(rpcConfig.getMap().size());
        serverNameStudMap = new HashMap<>(rpcConfig.getMap().size());

        for (Map.Entry<String, String> entry : rpcConfig.getMap().entrySet()) {
            String serverName = entry.getKey();
            String serverAddress = entry.getValue();
            String[] split = serverAddress.split(":");
            if (split.length != 2) {
                throw new RuntimeException("please check myn-grpc serverName :" + serverName);
            }

            try {
                //todo 这里面还可以加入拦截器,过滤器,超时等
                Integer port = NumberUtils.parseNumber(split[1], Integer.class);


                // channel (当前策略：每一个服务提供方配置一个 channel) TODO 待优化
                ManagedChannel channel = ManagedChannelBuilder.forAddress(split[0], port)
                        .keepAliveTime(60, TimeUnit.SECONDS)
                        .keepAliveTimeout(30, TimeUnit.SECONDS)
                        .idleTimeout(30, TimeUnit.SECONDS)
                        //使用虚拟线程
                        .executor(Executors.newVirtualThreadPerTaskExecutor())
                        .usePlaintext()
                        .build();

                System.out.println(Thread.currentThread());

                channelMap.put(serverAddress, channel);

                // stub
                BaseServiceGrpc.BaseServiceBlockingStub stub = BaseServiceGrpc.newBlockingStub(channel);

                serverNameStudMap.put(serverName, stub);
            } catch (Exception e) {
                logger.error("client启动异常 e", e);
                System.exit(-1);
            }
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (!CollectionUtils.isEmpty(channelMap)) {
            channelMap.forEach((k, channel) -> {
                if (Objects.nonNull(channel)) {
                    channel.shutdown();
                }
            });
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.run();
    }
}
