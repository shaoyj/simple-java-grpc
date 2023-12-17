package com.mylomen.grpc.client;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MynClientConfigurationSelector.class})
public @interface EnableMynRpcClient {


    /**
     * 需要扫描的路径
     *
     * @return 需要扫描的路径
     */
    String[] packages();


}
