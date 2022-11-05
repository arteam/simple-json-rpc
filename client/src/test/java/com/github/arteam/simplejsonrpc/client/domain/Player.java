package com.github.arteam.simplejsonrpc.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Objects;

public class Player {
    private String firstName;
    private String lastName;
    private Team team;
    private int number;
    private Position position;
    private Date birthDate;
    private double capHit;

    public Player(){}

    public Player(String firstName, String lastName,
                  Team team, int number,
                  Position position, Date birthDate,
                  double capHit) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.team = team;
        this.number = number;
        this.position = position;
        this.birthDate = birthDate;
        this.capHit = capHit;
    }

    @JsonProperty("firstName")
    public String firstName() {
        return firstName;
    }

    @JsonProperty("lastName")
    public String lastName() {
        return lastName;
    }

    @JsonProperty("team")
    public Team team() {
        return team;
    }

    @JsonProperty("number")
    public int number() {
        return number;
    }

    @JsonProperty("position")
    public Position position() {
        return position;
    }

    @JsonProperty("birthDate")
    public Date birthDate() {
        return birthDate;
    }

    @JsonProperty("capHit")
    public double capHit() {
        return capHit;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Player) obj;
        return Objects.equals(this.firstName, that.firstName) &&
                Objects.equals(this.lastName, that.lastName) &&
                Objects.equals(this.team, that.team) &&
                this.number == that.number &&
                Objects.equals(this.position, that.position) &&
                Objects.equals(this.birthDate, that.birthDate) &&
                Double.doubleToLongBits(this.capHit) == Double.doubleToLongBits(that.capHit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, team, number, position, birthDate, capHit);
    }

    @Override
    public String toString() {
        return "Player[" +
                "firstName=" + firstName + ", " +
                "lastName=" + lastName + ", " +
                "team=" + team + ", " +
                "number=" + number + ", " +
                "position=" + position + ", " +
                "birthDate=" + birthDate + ", " +
                "capHit=" + capHit + ']';
    }

}
