package com.mylomen.grpc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class FbClazzUtils {
    private final static Logger logger = LoggerFactory.getLogger(FbClazzUtils.class);

    private final static String RESOURCE_PATTERN = "/**/*.class";
    private final static String RESOURCE_CLASS_PATTERN = "classpath*:/**/*.class";

    public static <A extends Annotation> Set<Class<?>> loadClazzSet(String[] packageList, Class<A> filter) {
        Set<Class<?>> clazzSet = new HashSet<>();

        //获取指定路径下的全部类
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        for (String pkg : packageList) {
            try {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
                Resource[] resources = resourcePatternResolver.getResources(pattern);
                //MetadataReader 的工厂类
                MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
                for (Resource resource : resources) {
                    //用于读取类信息
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    //扫描到的class
                    String classname = reader.getClassMetadata().getClassName();
                    Class<?> clazz = Class.forName(classname);
                    //判断是否有指定主解
                    A anno = clazz.getAnnotation(filter);
                    if (anno != null) {
                        //将注解中的类型值作为key，对应的类作为 value
                        clazzSet.add(clazz);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("loadClazzSet_err packageList:{} e", packageList, e);
            }
        }

        return clazzSet;
    }

}
