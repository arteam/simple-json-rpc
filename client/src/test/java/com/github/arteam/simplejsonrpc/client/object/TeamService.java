package com.github.arteam.simplejsonrpc.client.object;

import com.github.arteam.simplejsonrpc.client.JsonRpcId;
import com.github.arteam.simplejsonrpc.client.domain.Player;
import com.github.arteam.simplejsonrpc.client.domain.Position;
import com.github.arteam.simplejsonrpc.client.domain.Team;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcOptional;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import com.google.common.base.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @JsonRpcMethod("findByInitials")
    Optional<Player> optionalFindByInitials(@JsonRpcParam("firstName") String firstName,
                                            @JsonRpcParam("lastName") String lastName);

    @JsonRpcMethod
    public List<Player> find(@JsonRpcOptional @JsonRpcParam("position") @Nullable Position position,
                             @JsonRpcOptional @JsonRpcParam("number") @Nullable int number,
                             @JsonRpcOptional @JsonRpcParam("team") @NotNull Optional<Team> team,
                             @JsonRpcOptional @JsonRpcParam("firstName") @Nullable String firstName,
                             @JsonRpcOptional @JsonRpcParam("lastName") @Nullable String lastName,
                             @JsonRpcOptional @JsonRpcParam("birthDate") @Nullable Date birthDate,
                             @JsonRpcOptional @JsonRpcParam("capHit") @NotNull Optional<Double> capHit);

    @JsonRpcMethod
    public List<Player> getPlayers();

    @JsonRpcMethod
    public Player getPlayer();

    public List<Player> bogusGetPlayers();

    @JsonRpcMethod
    public Player bogusFindByInitials(@JsonRpcParam("firstName") String firstName, String lastName);

    @JsonRpcMethod
    public Player findByCapHit(@JsonRpcParam("cap") double capHit);

    @JsonRpcMethod
    public long login(@JsonRpcParam("login") String login, @JsonRpcParam("password") String password);

    @JsonRpcMethod
    public long bogusCodeLogin(@JsonRpcParam("login") String login, @JsonRpcParam("password") String password);

    @JsonRpcMethod
    public long bogusMessageLogin(@JsonRpcParam("login") String login, @JsonRpcParam("password") String password);

    @JsonRpcMethod
    public Player bogusFind(@JsonRpcParam("firstName") String firstName,
                            @JsonRpcParam("firstName") String lastName,
                            @JsonRpcParam("age") int age);

    @JsonRpcMethod
    public List<Player> findPlayersByFirstNames(@JsonRpcParam("names") List<String> names);

    @JsonRpcMethod
    public List<Player> findPlayersByNumbers(@JsonRpcParam("numbers") int... numbers);

    @JsonRpcMethod
    <T> List<Player> genericFindPlayersByNumbers(@JsonRpcParam("numbers") T... numbers);

    @JsonRpcMethod
    LinkedHashMap<String, Double> getContractSums(@JsonRpcParam("contractLengths") Map<String, ? extends Number> contractLengths);
}
