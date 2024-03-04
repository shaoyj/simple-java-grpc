package com.mylomen.demo.service;

import org.springframework.web.bind.annotation.GetMapping;
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

//
//    @Resource
//    private RedisTemplate<String, String> redisTemplate;
//
//    @GetMapping("/redis")
//    public String redis() {
//        long epochMilli = Instant.now().toEpochMilli();
//        redisTemplate.opsForValue()
//                .set("test", "" + epochMilli, 10, TimeUnit.SECONDS);
//        return redisTemplate.opsForValue().get("test");
//    }
//
//
//    @Resource
//    private UserMapper userMapper;
//
//    @GetMapping("/mysql")
//    public Object sql() {
//        return userMapper.findOne();
//    }
//
//
//    @Resource(name = "restTemplate")
//    private RestTemplate restTemplate;
//
//    @GetMapping("/http")
//    public Object http() {
//
//        ResponseEntity<Map> mapResponseEntity = restTemplate.getForEntity(
//                "https://staging-api.freebeatfit.com/gos/marketplace/.FindCurrencyTypes", null, Map.class);
//        System.out.println(mapResponseEntity);
//        return "ok";
//    }

}
