package com.mylomen.grpc.constant;

public interface LogConstants {


    /**
     * UID
     */
    String UID = "UID";


    String TRACE_ID = "TRACE_ID";

    /**
     * 业务中 耗时过长的标记
     */
    String _COST_MONITOR = "fb_cost_monitor";

    /**
     * 超时时间 单位 秒
     */
    int _COST_MONITOR_TIMEOUT = 1;

}
