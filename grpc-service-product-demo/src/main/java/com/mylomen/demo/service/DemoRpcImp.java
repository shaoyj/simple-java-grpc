package com.mylomen.demo.service;

import com.mylomen.demo.api.DemoRpc;
import com.mylomen.demo.api.req.DemoReq;
import com.mylomen.gprc.client.domain.MynResponse;
import com.mylomen.grpc.server.MynGRpcServer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/v1/demo/")
@MynGRpcServer
public class DemoRpcImp implements DemoRpc {


    /**
     * 1: curl --location --request POST 'http://127.0.0.1:8083/v1/demo/hello' \
     * --header 'Content-Type: application/json' \
     * --data-raw '{
     * "uid":123
     * }'
     * <p>
     * 2: grpc
     *
     * @param req
     * @return
     */
    @PostMapping("hello")
    @Override
    public MynResponse<DemoReq> hello(@RequestBody DemoReq req) {
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return MynResponse.ok(req);
    }
}
