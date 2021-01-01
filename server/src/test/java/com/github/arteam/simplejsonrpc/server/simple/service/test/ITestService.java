package com.github.arteam.simplejsonrpc.server.simple.service.test;


import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;

@JsonRpcService
public interface ITestService {

    @JsonRpcMethod
    void method1();

}
