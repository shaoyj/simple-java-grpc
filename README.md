# info

Simple gprc scaffolding based on java language. 
Supports both grpc (main) and http. 
rcp interface must end with Rpc. 

# master

base jdk21 maven spring-boot(3.2.2)

# quick start
1. git clone https://gitee.com/Mylomen/simple-java-grpc.git
2. mvn clean compile
3. run grpc-service-provider-demo 
4. run grpc-service-consumer-demo 
5. curl http://localhost:8080/t1

# wrk 压测
```shell
wrk -t8 -c100 -d10s --latency http://127.0.0.1:8080/grpc
```
- VirtualThread(QPS) - 6000
- PlatformThread(QPS) - 1463

```shell
wrk -t8 -c200 -d10s --latency http://127.0.0.1:8080/grpc
```
- VirtualThread(QPS) - 13000
- PlatformThread(QPS) - 1465

```shell
wrk -t8 -c400 -d10s --latency http://127.0.0.1:8080/grpc
```
- VirtualThread(QPS) - 16500
- PlatformThread(QPS) - 1465


```shell
wrk -t8 -c800 -d10s --latency http://127.0.0.1:8080/grpc
```
- VirtualThread(QPS) - 16457
- PlatformThread(QPS) - 1460

# jdk other versions supported
> When upgrading jdk8 to jdk17 and later versions, please note that the paths of some tool packages have changed.

- Replace the springboot version with the matching jdk version
- change java.version 、maven.compiler.source、maven.compiler.target