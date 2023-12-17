package com.mylomen.grpc.client;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Map;

public class MynClientConfigurationSelector implements ImportSelector {
    private final Logger logger = LoggerFactory.getLogger(MynClientConfigurationSelector.class);

    @Getter
    private static String[] pkgList = null;

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableMynRpcClient.class.getName());
        if (attributes == null) {
            throw new RuntimeException("@EnableFbRpcClient not found");
        }

        //获取package属性的value
        String[] packages = (String[]) attributes.get("packages");
        if (packages == null || packages.length == 0 || ObjectUtils.isEmpty(packages[0])) {
            throw new RuntimeException("please check @EnableFbRpcClient package");
        }

        logger.info("需要扫描的 jar包 ：{}",
                Arrays.toString(packages));
        pkgList = packages;

        return new String[]{
                ScanJarForMynClient.class.getName(),
                MynGRpcClientConfig.class.getName(),
                GRpcClientCore.class.getName()};
    }


}
