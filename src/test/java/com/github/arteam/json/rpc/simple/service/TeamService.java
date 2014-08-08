package com.github.arteam.json.rpc.simple.service;

import com.github.arteam.json.rpc.simple.annotation.JsonRpcMethod;
import com.github.arteam.json.rpc.simple.annotation.JsonRpcOptional;
import com.github.arteam.json.rpc.simple.annotation.JsonRpcParam;
import com.github.arteam.json.rpc.simple.annotation.JsonRpcService;
import com.github.arteam.json.rpc.simple.domain.Player;
import com.github.arteam.json.rpc.simple.domain.Position;
import com.github.arteam.json.rpc.simple.domain.Team;
import com.github.arteam.json.rpc.simple.exception.BadCodeTeamServiceException;
import com.github.arteam.json.rpc.simple.exception.EmptyMessageTeamServiceException;
import com.github.arteam.json.rpc.simple.exception.TeamServiceAuthException;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Date: 7/27/14
 * Time: 8:46 PM
 *
 * @author Artem Prigoda
 */
@JsonRpcService
public class TeamService extends BaseService {

    private static final DateTimeFormatter fmt = ISODateTimeFormat.date().withZone(DateTimeZone.UTC);

    private List<Player> players = Lists.newArrayList(ImmutableList.<Player>builder()
            .add(new Player("David", "Backes", new Team("St. Louis Blues", "NHL"), 42, Position.CENTER, date("1984-05-01"), 4.5))
            .add(new Player("Vladimir", "Tarasenko", new Team("St. Louis Blues", "NHL"), 91, Position.RIGHT_WINGER, date("1991-12-13"), 0.9))
            .add(new Player("Jack", "Allen", new Team("St. Louis Blues", "NHL"), 34, Position.GOALTENDER, date("1990-08-07"), 0.5))
            .add(new Player("Jay", "Bouwmeester", new Team("St. Louis Blues", "NHL"), 19, Position.DEFENDER, date("1985-08-07"), 5.4))
            .add(new Player("Steven", "Stamkos", new Team("Tampa Bay Lightning", "NHL"), 91, Position.CENTER, date("1990-02-07"), 7.5))
            .add(new Player("Ryan", "Callahan", new Team("Tampa Bay Lightning", "NHL"), 24, Position.RIGHT_WINGER, date("1985-03-21"), 5.8))
            .add(new Player("Ben", "Bishop", new Team("Tampa Bay Lightning", "NHL"), 30, Position.GOALTENDER, date("1986-11-21"), 2.3))
            .add(new Player("Victor", "Hedman", new Team("Tampa Bay Lightning", "NHL"), 77, Position.DEFENDER, date("1990-12-18"), 4.0))
            .build());

    @JsonRpcMethod
    public boolean add(@JsonRpcParam("player") Player s) {
        return players.add(s);
    }

    @JsonRpcMethod("find_by_birth_year")
    public List<Player> findByBirthYear(@JsonRpcParam("birth_year") final int birthYear) {
        return ImmutableList.copyOf(Iterables.filter(players, new Predicate<Player>() {
            @Override
            public boolean apply(Player player) {
                int year = new DateTime(player.getBirthDate()).getYear();
                return year == birthYear;
            }
        }));
    }

    @JsonRpcMethod
    public Player findByInitials(@JsonRpcParam("firstName") final String firstName,
                                 @JsonRpcParam("lastName") final String lastName) {
        return Iterables.tryFind(players, new Predicate<Player>() {
            @Override
            public boolean apply(Player player) {
                return player.getFirstName().equals(firstName) &&
                        player.getLastName().equals(lastName);
            }
        }).orNull();
    }

    @JsonRpcMethod
    public List<Player> find(@JsonRpcOptional @JsonRpcParam("position") @Nullable final Position position,
                             @JsonRpcOptional @JsonRpcParam("number") @Nullable final int number,
                             @JsonRpcOptional @JsonRpcParam("team") @Nullable final Team team,
                             @JsonRpcOptional @JsonRpcParam("firstName") @Nullable final String firstName,
                             @JsonRpcOptional @JsonRpcParam("lastName") @Nullable final String lastName,
                             @JsonRpcOptional @JsonRpcParam("birthDate") @Nullable final Date birthDate,
                             @JsonRpcOptional @JsonRpcParam("capHit") @Nullable final double capHit) {
        return Lists.newArrayList(Iterables.filter(players, new Predicate<Player>() {
            @Override
            public boolean apply(Player player) {
                if (position != null && !player.getPosition().equals(position)) return false;
                if (number != 0 && player.getNumber() != number) return false;
                if (team != null && !player.getTeam().equals(team)) return false;
                if (firstName != null && !player.getFirstName().equals(firstName)) return false;
                if (lastName != null && !player.getLastName().equals(lastName)) return false;
                if (birthDate != null && !player.getBirthDate().equals(birthDate)) return false;
                if (capHit != 0 && player.getCapHit() != capHit) return false;
                return true;
            }
        }));
    }

    @JsonRpcMethod
    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> bogusGetPlayers() {
        return players;
    }

    @JsonRpcMethod
    private List<Player> privateGetPlayers() {
        return players;
    }

    @JsonRpcMethod
    public Player bogusFindByInitials(@JsonRpcParam("firstName") String firstName, String lastName) {
        return findByInitials(firstName, lastName);
    }

    @JsonRpcMethod
    public Player findByCapHit(@JsonRpcParam("cap") double capHit) {
        throw new IllegalStateException("Not implemented");
    }

    @JsonRpcMethod
    public long login(@JsonRpcParam("login") String login, @JsonRpcParam("password") String password) {
        if (!login.equals("CAFE") && !password.equals("BABE")) {
            throw new TeamServiceAuthException("Not authorized");
        }
        return 0xCAFEBABE;
    }

    @JsonRpcMethod
    public long bogusCodeLogin(@JsonRpcParam("login") String login, @JsonRpcParam("password") String password) {
        if (!login.equals("CAFE") && !password.equals("BABE")) {
            throw new BadCodeTeamServiceException("Not authorized");
        }
        return 0xCAFEBABE;
    }

    @JsonRpcMethod
    public long bogusMessageLogin(@JsonRpcParam("login") String login, @JsonRpcParam("password") String password) {
        if (!login.equals("CAFE") && !password.equals("BABE")) {
            throw new EmptyMessageTeamServiceException("Not authorized");
        }
        return 0xCAFEBABE;
    }

    @JsonRpcMethod
    public Player bogusFind(@JsonRpcParam("firstName") String firstName,
                            @JsonRpcParam("firstName") String lastName,
                            @JsonRpcParam("age") int age) {
        return null;
    }

    @JsonRpcMethod
    public List<Player> findPlayersByFirstNames(@JsonRpcParam("names") final List<String> names) {
        return Lists.newArrayList(Iterables.filter(players, new Predicate<Player>() {
            @Override
            public boolean apply(Player player) {
                return names.contains(player.getFirstName());
            }
        }));
    }

    @JsonRpcMethod
    public List<Player> findPlayersByNumbers(@JsonRpcParam("numbers") final int... numbers) {
        return Lists.newArrayList(Iterables.filter(players, new Predicate<Player>() {
            @Override
            public boolean apply(Player player) {
                for (int number : numbers) {
                    if (player.getNumber() == number) {
                        return true;
                    }
                }
                return false;
            }
        }));
    }

    @JsonRpcMethod
    public <T> List<Player> genericFindPlayersByNumbers(@JsonRpcParam("numbers") final T... numbers) {
        return Lists.newArrayList(Iterables.filter(players, new Predicate<Player>() {
            @Override
            public boolean apply(Player player) {
                for (T number : numbers) {
                    if (String.valueOf(player.getNumber()).equals(number.toString())) {
                        return true;
                    }
                }
                return false;
            }
        }));
    }

    @JsonRpcMethod
    public Map<String, Double> getContractSums(@JsonRpcParam("contractLengths") Map<String, ? extends Number> contractLengths) {
        Map<String, Double> playerContractSums = Maps.newLinkedHashMap();
        for (Player player : players) {
            playerContractSums.put(player.getLastName(),
                    player.getCapHit() * contractLengths.get(player.getLastName()).intValue());
        }
        return playerContractSums;
    }


    @JsonRpcMethod
    public static Date date(@JsonRpcParam("textDate") String textDate) {
        return fmt.parseDateTime(textDate).toDate();
    }

}
