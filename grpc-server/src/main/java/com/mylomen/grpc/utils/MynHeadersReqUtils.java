package com.mylomen.grpc.utils;


import com.mylomen.gprc.client.domain.MynHeadersReq;
import com.mylomen.grpc.constant.CommonConstants;
import com.mylomen.grpc.constant.LogConstants;
import org.slf4j.MDC;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Objects;


public class MynHeadersReqUtils {

    public static void ParseHeadersInfo(MynHeadersReq req, Map<String, Object> map) {
        if (Objects.isNull(req) || map == null) {
            return;
        }

        //uid
        Long uid = req.getUid();
        if (NumUtils.isNullOrZero(uid)) {
            req.setUid(FbMapUtils.getLongValue(map, CommonConstants.Header.UID));
        }

        //traceId
        String traceId = req.getTraceId();
        if (ObjectUtils.isEmpty(traceId)) {
            req.setTraceId(FbMapUtils.getStringValue(map, CommonConstants.Header.TRACE_ID));
        }

        //clientIp
        String clientIp = req.getClientIp();
        if (ObjectUtils.isEmpty(clientIp)) {
            req.setClientIp(FbMapUtils.getStringValue(map, CommonConstants.Header.CLIENT_IP));
        }
    }


    public static void fillLog(MynHeadersReq request) {
        //traceId
        String traceId = request.getTraceId();
        if (!ObjectUtils.isEmpty(traceId) && ObjectUtils.isEmpty(MDC.get(LogConstants.TRACE_ID))) {
            MDC.put(LogConstants.TRACE_ID, traceId);
        }

        //uid
        Long uid = request.getUid();
        if (NumUtils.noNullAndZero(uid) && ObjectUtils.isEmpty(MDC.get(LogConstants.UID))) {
            MDC.put(LogConstants.UID, uid + "");
        }
    }


}
