package com.mylomen.grpc.utils;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

public class AppContextUtil {

    @Getter
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppContextUtil.applicationContext = applicationContext;
    }


    public static void publishEvent(ApplicationEvent event) {
        AppContextUtil.applicationContext.publishEvent(event);
    }

    public AppContextUtil() {
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public static Object getBean(Class<?> beanType) {
        return applicationContext.getBean(beanType);
    }

    public static <T> T getBeanAuto(String beanName) {
        return (T) applicationContext.getBean(beanName);
    }

    public static <T> T getBeanAuto(Class<T> beanType) {
        return applicationContext.getBean(beanType);
    }

    public static String getProperty(String key) {
        return applicationContext.getEnvironment().getProperty(key);
    }
}
