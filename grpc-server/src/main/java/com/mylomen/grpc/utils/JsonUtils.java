package com.mylomen.grpc.utils;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 待优化
 */
public class JsonUtils {

    private final static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    public static String toJson(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        return JSON.toJSONString(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (ObjectUtils.isEmpty(json)) {
            return null;
        }

        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            logger.error("fromJson_err json:{} clazz:{} e", json, clazz, e);
        }
        return null;
    }

    /**
     * @param json
     * @return 不能为空
     */
    public static Map<String, Object> parseMap(byte[] json) {
        if (Objects.isNull(json)) {
            return Collections.emptyMap();
        }

        try {
            return JSON.parseObject(json, Map.class);
        } catch (Exception e) {
            logger.error("parseMap_err json:{}   e", json, e);
        }

        return Collections.emptyMap();
    }


    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        if (ObjectUtils.isEmpty(json)) {
            return null;
        }

        try {
            return JSON.parseArray(json, clazz);
        } catch (Exception e) {
            logger.error("parseArray_err json:{} clazz:{} e", json, clazz, e);
        }
        return null;
    }

    public static <T> T parseObject(byte[] bytes, Type type) {
        if (ObjectUtils.isEmpty(bytes)) {
            return null;
        }

        try {
            return JSON.parseObject(bytes, type);
        } catch (Exception e) {
            logger.error("parseObject_err bytes:{} clazz:{} e", bytes, type, e);
        }
        return null;
    }
}
