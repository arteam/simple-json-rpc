package com.github.arteam.simplejsonrpc.client.object;

import com.github.arteam.simplejsonrpc.client.JsonRpcId;
import com.github.arteam.simplejsonrpc.client.domain.Player;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;

import java.util.List;

/**
 * Date: 24.08.14
 * Time: 18:02
 *
 * @author Artem Prigoda
 */
@JsonRpcService
@JsonRpcId(value = TestIdGenerator.class)
public interface TeamService {

    @JsonRpcMethod
    boolean add(@JsonRpcParam("player") Player s);

    @JsonRpcMethod("find_by_birth_year")
    List<Player> findByBirthYear(@JsonRpcParam("birth_year") int birthYear);

    @JsonRpcMethod
    Player findByInitials(@JsonRpcParam("firstName") String firstName,
                          @JsonRpcParam("lastName") String lastName);

    @JsonRpcMethod
    List<Player> getPlayers();
}
