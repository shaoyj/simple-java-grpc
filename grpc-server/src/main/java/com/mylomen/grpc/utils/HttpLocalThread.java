package com.mylomen.grpc.utils;


import com.mylomen.grpc.constant.CommonConstants;
import com.mylomen.grpc.constant.LogConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Objects;

public class HttpLocalThread {

    static class HttpContext {
        private HttpServletRequest request;
        private HttpServletResponse response;

        public HttpContext(HttpServletRequest request,
                           HttpServletResponse response) {
            super();
            this.request = request;
            this.response = response;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        public HttpServletResponse getResponse() {
            return response;
        }


    }

    private static ThreadLocal<HttpContext> threadLocal = new ThreadLocal<>();

    public static void set(HttpServletRequest req, HttpServletResponse res) {
        threadLocal.set(new HttpContext(req, res));
    }

    public static HttpServletRequest getRequest() {
        HttpContext httpContext = threadLocal.get();
        if (Objects.isNull(httpContext)) {
            return null;
        }
        return httpContext.getRequest();
    }


    public static HttpServletResponse getResponse() {
        HttpContext httpContext = threadLocal.get();
        if (Objects.isNull(httpContext)) {
            return null;
        }
        return httpContext.getResponse();
    }

    public static void clean() {
        threadLocal.remove();
    }

    public static String GetHeaderValue(String key) {
        key = key.toLowerCase();

        String val = MynRpcContent.getRpcCtxString(key);
        if (ObjectUtils.isNotEmpty(val)) {
            return val;
        }

        HttpServletRequest request = getRequest();
        return Objects.isNull(request) ? null : request.getHeader(key);
    }

    public static String GetHeaderValue(String key, HttpServletRequest request) {
        key = key.toLowerCase();

        String val = MynRpcContent.getRpcCtxString(key);
        if (ObjectUtils.isNotEmpty(val)) {
            return val;
        }

        return Objects.isNull(request) ? null : request.getHeader(key);
    }


    public static String parseClientIp() {
        //转成小写
        String key = CommonConstants.Header.CLIENT_IP.toLowerCase();
        String val = MynRpcContent.getRpcCtxString(key);
        if (ObjectUtils.isNotEmpty(val)) {
            return val;
        }

        HttpServletRequest request = getRequest();
        return Objects.isNull(request) ? null : MynIpUtils.getIpAddress(request);
    }


    public static String parseUrl() {
        HttpServletRequest request = getRequest();
        return Objects.isNull(request) ? null : request.getRequestURI();
    }


    /**
     * @return 当前登录的用户id
     */
    public static Long parseUid() {
        //优先从 请求头获取
        HttpServletRequest request = getRequest();
        if (Objects.nonNull(request)) {
            Object userId = request.getAttribute(LogConstants.UID.toLowerCase());
            if (Objects.nonNull(userId)) {
                long uid = NumberUtils.toLong(userId.toString());
                if (uid > 0) {
                    return uid;
                }
            }
        }

        String val = GetHeaderValue(LogConstants.UID);
        if (ObjectUtils.isNotEmpty(val)) {
            return NumberUtils.toLong(val);
        }

        return 0L;
    }
}
