
package com.mylomen.demo;


import com.mylomen.grpc.server.EnableMynRpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableMynRpcServer(packages = {"com.mylomen.demo.service"})
public class LocalApplication {


    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(LocalApplication.class, args);
    }


}
