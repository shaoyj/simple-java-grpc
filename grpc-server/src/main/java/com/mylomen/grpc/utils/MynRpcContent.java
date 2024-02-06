package com.mylomen.grpc.utils;

import org.springframework.core.NamedInheritableThreadLocal;

import java.util.Map;
import java.util.Objects;

/**
 *
 */
public class MynRpcContent {

    private static final ThreadLocal<Map<String, Object>> inheritableRequestAttributesHolder = new NamedInheritableThreadLocal<>("fbRpc ctx");


    public static void setValue(Map<String, Object> map) {
        if (Objects.nonNull(map) && !map.isEmpty()) {
            inheritableRequestAttributesHolder.set(map);
        }
    }


    public static void clear() {
        inheritableRequestAttributesHolder.remove();
    }


    public static String getRpcCtxString(String key) {
        Map<String, Object> map = inheritableRequestAttributesHolder.get();
        if (Objects.isNull(map) || map.isEmpty()) {
            return null;
        }

        return MynMapUtils.getStringValue(map, key);
    }

    public static Integer getRpcCtxInt(String key) {
        Map<String, Object> map = inheritableRequestAttributesHolder.get();
        if (Objects.isNull(map) || map.isEmpty()) {
            return null;
        }

        return MynMapUtils.getIntegerValue(map, key);
    }

    public static Long getRpcCtxLong(String key) {
        Map<String, Object> map = inheritableRequestAttributesHolder.get();
        if (Objects.isNull(map) || map.isEmpty()) {
            return null;
        }

        return MynMapUtils.getLongValue(map, key);
    }
}
