package com.mylomen.grpc.client;


import com.mylomen.grpc.utils.AppContextUtil;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;


@ConfigurationProperties(prefix = "myn-grpc")
public class MynGRpcClientConfig implements ApplicationContextAware {

    private Map<String, String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        if (AppContextUtil.getApplicationContext() == null) AppContextUtil.setApplicationContext(ctx);
    }
}
