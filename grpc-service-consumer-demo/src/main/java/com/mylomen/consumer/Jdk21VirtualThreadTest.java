package com.mylomen.consumer;

import com.mylomen.demo.api.DemoRpc;
import com.mylomen.demo.api.req.DemoReq;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class Jdk21VirtualThreadTest {

    @GetMapping("/what")
    public String what() {
        System.out.println(Thread.currentThread());
        return "what";
    }

    @GetMapping("/sleep")
    public String sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "ok";
    }


    @Resource
    private DemoRpc demoRpc;

    @RequestMapping("/grpc")
    public Object grpc() {
        DemoReq req = new DemoReq();
        req.setUid(123L);
        return demoRpc.hello(req);
    }

}
