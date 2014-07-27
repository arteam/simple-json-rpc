package com.github.arteam.dropwizard.json.rpc.service;

import com.github.arteam.dropwizard.json.rpc.protocol.domain.JsonRpcMethod;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.JsonRpcParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 7/27/14
 * Time: 8:46 PM
 *
 * @author Artem Prigoda
 */
public class TeamService  {

    private List<String> players = new ArrayList<String>();

    @JsonRpcMethod
    public boolean add(@JsonRpcParam("player") String s) {
        return players.add(s);
    }

    @JsonRpcMethod
    public boolean remove(@JsonRpcParam("player") String o) {
        return players.remove(o);
    }

    @JsonRpcMethod
    public String get(@JsonRpcParam("index") int index) {
        return players.get(index);
    }

    @JsonRpcMethod
    public List<String> getPlayers(){
        return players;
    }
}
