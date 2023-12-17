package com.mylomen.grpc.constant;

/**
 * @description: 通用常量
 * @author: smalljop
 * @create: 2020-02-12 12:02
 **/
public interface CommonConstants {

    interface Header {

        String UID = "uid";

        /**
         * 日志跟踪标识
         */
        String TRACE_ID = "TRACE_ID";

        /**
         * 请求方IP
         */
        String CLIENT_IP = "CLIENT_IP";

    }
}
