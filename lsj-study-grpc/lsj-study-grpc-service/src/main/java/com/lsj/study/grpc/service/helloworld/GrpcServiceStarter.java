package com.lsj.study.grpc.service.helloworld;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * @author lishangjian
 * @date 2024/3/27 17:39
 */
public class GrpcServiceStarter {
    public static void main(String[] args) throws Exception  {
        int port = 9091;
        Server server = ServerBuilder
                .forPort(port)
                .addService(new HelloWorldService())
                .build()
                .start();
        System.out.println("server started, port : " + port);
        server.awaitTermination();
    }
}
