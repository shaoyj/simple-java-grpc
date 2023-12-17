package com.mylomen.gprc.client;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
public @interface MynGrpcClient {


    /**
     * 服务名称
     * @return
     */
    String serviceName();

}
