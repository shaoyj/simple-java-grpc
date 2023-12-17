package com.mylomen.grpc.client;


import com.mylomen.gprc.client.MynGrpcClient;
import com.mylomen.grpc.utils.FbClazzUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Set;

public class ScanJarForMynClient implements BeanDefinitionRegistryPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(ScanJarForMynClient.class);

    public void run(BeanDefinitionRegistry registry) {
        //扫描到的目标类集合
        Set<Class<?>> targetClazzSet = FbClazzUtils.loadClazzSet(MynClientConfigurationSelector.getPkgList(), MynGrpcClient.class);
        if (CollectionUtils.isEmpty(targetClazzSet)) {
            throw new RuntimeException("loadClazzSet_empty : " + Arrays.toString(MynClientConfigurationSelector.getPkgList()));
        }

        for (Class<?> cls : targetClazzSet) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cls);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.setAutowireCandidate(true);

            //注入代理 factory
            definition.getPropertyValues().add("annotationClazz", definition.getBeanClassName());
            definition.setBeanClass(MynClientProxyFactory.class);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);

            registry.registerBeanDefinition(cls.getSimpleName(), definition);
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        run(registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}