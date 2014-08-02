package com.github.arteam.json.rpc.simple.service;

import com.github.arteam.json.rpc.simple.annotation.JsonRpcMethod;
import com.github.arteam.json.rpc.simple.annotation.JsonRpcService;

/**
 * Date: 8/2/14
 * Time: 6:25 PM
 *
 * @author Artem Prigoda
 */
@JsonRpcService
public class BogusService {

    @JsonRpcMethod
    public void bogus(){
        System.out.println("Bogus");
    }

    @JsonRpcMethod("bogus")
    public void bogus2(){
        System.out.println("Bogus2");
    }

    @JsonRpcMethod
    public void notBogus(){
        System.out.println("Not bogus");
    }
}
