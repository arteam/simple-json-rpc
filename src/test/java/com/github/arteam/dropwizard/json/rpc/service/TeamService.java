package com.github.arteam.dropwizard.json.rpc.service;

import com.github.arteam.dropwizard.json.rpc.domain.Player;
import com.github.arteam.dropwizard.json.rpc.domain.Position;
import com.github.arteam.dropwizard.json.rpc.domain.Team;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.JsonRpcMethod;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.JsonRpcParam;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.tz.FixedDateTimeZone;

import java.util.Date;
import java.util.List;

/**
 * Date: 7/27/14
 * Time: 8:46 PM
 *
 * @author Artem Prigoda
 */
public class TeamService {

    private static final DateTimeFormatter fmt = ISODateTimeFormat.date().withZone(DateTimeZone.UTC);

    private List<Player> players = Lists.newArrayList(ImmutableList.<Player>builder()
            .add(new Player("David", "Backes", new Team("St. Louis Blues", "NHL"), 42, Position.CENTER, date("1984-05-01"), 4.5))
            .add(new Player("Vladimir", "Tarasenko", new Team("St. Louis Blues", "NHL"), 91, Position.RIGHT_WINGER, date("1991-12-13"), 0.9))
            .add(new Player("Jack", "Allen", new Team("St. Louis Blues", "NHL"), 34, Position.GOALTENDER, date("1990-08-07"), 0.5))
            .add(new Player("Jay", "Bouwmeester", new Team("St. Louis Blues", "NHL"), 19, Position.DEFENDER, date("1990-08-07"), 5.4))
            .add(new Player("Steven", "Stamkos", new Team("Tampa Bay Lightning", "NHL"), 91, Position.CENTER, date("1990-02-07"), 7.5))
            .add(new Player("Ryan", "Callahan", new Team("Tampa Bay Lightning", "NHL"), 24, Position.RIGHT_WINGER, date("1985-03-21"), 5.8))
            .add(new Player("Ben", "Bishop", new Team("Tampa Bay Lightning", "NHL"), 30, Position.GOALTENDER, date("1986-11-21"), 2.3))
            .add(new Player("Victor", "Hedman", new Team("Tampa Bay Lightning", "NHL"), 77, Position.DEFENDER, date("1990-12-18"), 4.0))
            .build());

    @JsonRpcMethod
    public boolean add(@JsonRpcParam("player") Player s) {
        return players.add(s);
    }

    @JsonRpcMethod
    public List<Player> findByBirthYear(@JsonRpcParam("birthYear") final int birthYear) {
        return Lists.newArrayList(Iterables.filter(players, new Predicate<Player>() {
            @Override
            public boolean apply(Player player) {
                return new DateTime(player.getBirthDate()).getYear() == birthYear;
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
    public List<Player> find(@Optional @JsonRpcParam("position") @Nullable final Position position,
                             @Optional @JsonRpcParam("number") @Nullable final int number,
                             @Optional @JsonRpcParam("team") @Nullable final Team team,
                             @Optional @JsonRpcParam("firstName") @Nullable final String firstName,
                             @Optional @JsonRpcParam("lastName") @Nullable final String lastName,
                             @Optional @JsonRpcParam("birthDate") @Nullable final Date birthDate,
                             @Optional @JsonRpcParam("capHit") @Nullable final double capHit) {
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

    private static Date date(String textDate) {
        return fmt.parseDateTime(textDate).toDate();
    }
}
