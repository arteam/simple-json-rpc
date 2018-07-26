package com.github.arteam.simplejsonrpc.server.simple.service;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;

/**
 * Date: 7/30/14
 * Time: 3:29 PM
 */
public class BaseService {

    @JsonRpcMethod
    public boolean isAlive() {
        return true;
    }

    @JsonRpcMethod
    public void updateCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Updating cache...");
                // Some time...
                System.out.println("Done!");
            }
        }).start();
    }
}
