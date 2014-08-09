package com.github.arteam.simplejsonrpc.client.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.Date;

public class Player {

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private Team team;

    @JsonProperty
    private int number;

    @JsonProperty
    private Position position;

    @JsonProperty
    private Date birthDate;

    @JsonProperty
    private double capHit;

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
        return Objects.toStringHelper(this)
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
