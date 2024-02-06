package com.mylomen.demo.api.req;

import com.mylomen.gprc.client.domain.MynHeadersReq;
import lombok.Data;

@Data
public class DemoReq extends MynHeadersReq {

    private String demo;
}
