package com.mylomen.demo.api;

import com.mylomen.demo.api.req.DemoReq;
import com.mylomen.gprc.client.MynGrpcClient;
import com.mylomen.gprc.client.domain.MynResponse;

@MynGrpcClient(serviceName = "grpc-service-demo")
public interface DemoRpc {

    MynResponse<DemoReq> hello(DemoReq req);
}
