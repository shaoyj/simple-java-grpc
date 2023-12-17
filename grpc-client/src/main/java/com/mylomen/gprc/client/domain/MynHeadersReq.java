package com.mylomen.gprc.client.domain;


import lombok.Data;

import java.io.Serializable;

@Data
public class MynHeadersReq implements Serializable {

    private Long uid;

    private String authorization;

    private String traceId;

    private String clientIp;

}
