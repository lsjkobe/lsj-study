package com.lsj.study.grpc.client.helloworld;

import com.lsj.study.grpc.service.helloworld.GreeterGrpc;
import com.lsj.study.grpc.service.helloworld.HelloReply;
import com.lsj.study.grpc.service.helloworld.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * @author lishangjian
 * @date 2024/3/27 17:46
 */
public class HelloWorldClient {

    public static void main(String[] args) {
        ManagedChannel channel =
                ManagedChannelBuilder.forAddress("127.0.0.1", 9091).usePlaintext().build();
        GreeterGrpc.GreeterBlockingStub greeterBlockingStub = GreeterGrpc.newBlockingStub(channel);
        HelloReply reply = greeterBlockingStub.sayHello(new HelloRequest());
    }
}
