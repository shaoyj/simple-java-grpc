package com.mylomen.grpc.server;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Map;

public class MynServerConfigurationSelector implements ImportSelector {
    private final Logger logger = LoggerFactory.getLogger(MynServerConfigurationSelector.class);

    @Getter
    private static String[] pkgList = null;

    @Getter
    private static int port = 8081;

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableMynRpcServer.class.getName());
        if (attributes == null) {
            throw new RuntimeException("@EnableFbRpcServer not found");
        }

        //获取package属性的value
        String[] packages = (String[]) attributes.get("packages");
        if (packages == null || packages.length == 0 || ObjectUtils.isEmpty(packages[0])) {
            throw new RuntimeException("please check @EnableFbRpcServer package");
        }

        port = (int) attributes.get("port");
        pkgList = packages;
        logger.info("提供rpc服务，需要扫描的包 ：{}", Arrays.toString(pkgList));

        return new String[]{
                MynServerGRpcImplProxy.class.getName(),
                GRpcServerCore.class.getName()
        };
    }

}
