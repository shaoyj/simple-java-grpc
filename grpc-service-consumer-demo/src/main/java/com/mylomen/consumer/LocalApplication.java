
package com.mylomen.consumer;


import com.mylomen.grpc.client.EnableMynRpcClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableMynRpcClient(packages = {"com.mylomen.demo.api"})
@EnableAsync
public class LocalApplication {


    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(LocalApplication.class, args);
    }


}
