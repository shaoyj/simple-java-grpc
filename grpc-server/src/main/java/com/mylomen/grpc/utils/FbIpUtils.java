package com.mylomen.grpc.utils;

import jakarta.servlet.http.HttpServletRequest;

public class FbIpUtils {

    /**
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        //目前则是网关ip
        String ip = request.getHeader("X-Real-IP");
        if (ip != null && !"".equals(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !"".equals(ip) && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            if (index != -1) {
                //只获取第一个值
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {

            return request.getRemoteAddr();
        }
    }
}
