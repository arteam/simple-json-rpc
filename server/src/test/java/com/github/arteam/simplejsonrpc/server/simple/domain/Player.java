package com.github.arteam.simplejsonrpc.server.simple.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Date;

/**
 * Date: 7/29/14
 * Time: 12:49 PM
 */
public class Player {

    private final String firstName;
    private final String lastName;
    private final Team team;
    private final int number;
    private final Position position;
    private final Date birthDate;
    private final double capHit;

    @JsonCreator
    public Player(@JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName,
                  @JsonProperty("team") Team team, @JsonProperty("number") int number, @JsonProperty("position") Position position,
                  @JsonProperty("birthDate") Date birthDate, @JsonProperty("capHit") double capHit) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.number = number;
        this.position = position;
        this.birthDate = birthDate;
        this.capHit = capHit;
        this.team = team;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getNumber() {
        return number;
    }

    public Position getPosition() {
        return position;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public double getCapHit() {
        return capHit;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("team", team)
                .add("number", number)
                .add("position", position)
                .add("birthDate", birthDate)
                .add("capHit", capHit)
                .toString();
    }
}
