package com.mylomen.grpc.server;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MynServerConfigurationSelector.class})
public @interface EnableMynRpcServer {


    /**
     * 扫描路径
     *
     * @return 包路径
     */
    String[] packages();


    /**
     * 服务端口
     *
     * @return
     */
    int port() default 8081;
}
