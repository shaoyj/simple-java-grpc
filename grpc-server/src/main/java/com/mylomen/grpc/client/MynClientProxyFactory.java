package com.mylomen.grpc.client;

import com.mylomen.gprc.client.MynGrpcClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.lang.Nullable;

public class MynClientProxyFactory<T> implements FactoryBean<T> {

    private Class<T> annotationClazz;


    public Class<T> getAnnotationClazz() {
        return annotationClazz;
    }

    public void setAnnotationClazz(Class<T> annotationClazz) {
        this.annotationClazz = annotationClazz;
    }


    @SuppressWarnings("unchecked")
    @Override
    public T getObject() {
        MynGrpcClient annotation = annotationClazz.getAnnotation(MynGrpcClient.class);

        return (T) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class<?>[]{annotationClazz},
                new GRpcImplProxy(annotation.serviceName()));
    }


    @Override
    @Nullable
    public Class<?> getObjectType() {
        return annotationClazz;
    }


}