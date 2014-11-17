package com.github.arteam.simplejsonrpc.client.object;

import com.github.arteam.simplejsonrpc.client.JsonRpcParams;
import com.github.arteam.simplejsonrpc.client.ParamsType;
import com.github.arteam.simplejsonrpc.client.domain.Player;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;

import java.util.List;

/**
 * Date: 11/17/14
 * Time: 9:59 PM
 *
 * @author Artem Prigoda
 */
@JsonRpcService
public interface BaseService {

    @JsonRpcMethod
    @JsonRpcParams(ParamsType.ARRAY)
    List<Player> getPlayers();

    @JsonRpcMethod
    @JsonRpcParams(ParamsType.ARRAY)
    Player getPlayer();

    @JsonRpcMethod
    long login(@JsonRpcParam("login") String login, @JsonRpcParam("password") String password);
}
