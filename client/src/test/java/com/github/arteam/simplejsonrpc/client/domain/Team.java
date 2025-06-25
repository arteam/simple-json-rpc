package com.github.arteam.simplejsonrpc.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Team {
    private String name;
    private String league;

    public Team(){}

    public Team(String name, String league) {
        this.name = name;
        this.league = league;
    }

    @JsonProperty
    public String name() {
        return name;
    }

    @JsonProperty
    public String league() {
        return league;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Team that = (Team) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.league, that.league);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, league);
    }

    @Override
    public String toString() {
        return "Team[" +
                "name=" + name + ", " +
                "league=" + league + ']';
    }

}
