package com.lsj.study.grpc.service.helloworld;

import io.grpc.stub.StreamObserver;

/**
 * @author lishangjian
 * @date 2024/3/22 11:44
 */
public class HelloWorldService extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        super.sayHello(request, responseObserver);
    }

    @Override
    public void sayHelloAgain(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        super.sayHelloAgain(request, responseObserver);
    }
}
