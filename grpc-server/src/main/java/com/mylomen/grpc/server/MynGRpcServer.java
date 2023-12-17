package com.mylomen.grpc.server;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
public @interface MynGRpcServer {
    //待扩展
}
