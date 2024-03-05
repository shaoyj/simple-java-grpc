package com.mylomen.consumer;

import com.mylomen.consumer.config.SyncServiceTest;
import com.mylomen.demo.api.DemoRpc;
import com.mylomen.demo.api.req.DemoReq;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
public class Jdk21VirtualThreadTest {

    @GetMapping("/what")
    public String what() {
        System.out.println(Thread.currentThread());
        return "what";
    }

    @GetMapping("/sleep")
    public String sleep() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(50);
        return "ok";
    }


    @Resource
    private SyncServiceTest syncServiceTest;

    /**
     * 测试异步性能 虚拟线程 vs 平台线程.
     * 注意: 异步方法 execute 不能和调用者在同一个类中
     * 注意: 启动类需要有 @EnableAsync
     *
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/async")
    public long async() throws InterruptedException {
        long start = System.currentTimeMillis();
        int size = 100;
        final CountDownLatch latch = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            syncServiceTest.execute(latch);
        }
        latch.await();
        return System.currentTimeMillis() - start;
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
