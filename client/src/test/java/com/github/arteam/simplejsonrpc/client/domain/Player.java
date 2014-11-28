package com.github.arteam.simplejsonrpc.client.domain;

import com.google.common.base.Objects;

import java.util.Date;

public class Player {

    private String firstName;

    private String lastName;

    private Team team;

    private int number;

    private Position position;

    private Date birthDate;

    private double capHit;

    public Player(String firstName, String lastName, Team team, int number, Position position,
                  Date birthDate, double capHit) {
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
