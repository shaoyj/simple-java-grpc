package com.mylomen.consumer;

import com.mylomen.demo.api.DemoRpc;
import com.mylomen.demo.api.req.DemoReq;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Resource
    private DemoRpc demoRpc;

    @RequestMapping("health")
    public String health() {
        return "ok";
    }

    @RequestMapping("t1")
    public Object t1() {
        DemoReq req = new DemoReq();
        req.setUid(123L);
        return demoRpc.hello(req);
    }
}
