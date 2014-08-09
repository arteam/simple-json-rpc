package com.github.arteam.simplejsonrpc.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class Team {

    @JsonProperty
    private final String name;

    @JsonProperty
    private final String league;

    public Team(@JsonProperty("name") String name, @JsonProperty("league") String league) {
        this.name = name;
        this.league = league;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, league);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Team) {
            Team other = (Team) obj;
            return Objects.equal(name, other.name) &&
                    Objects.equal(league, other.league);
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("league", league)
                .toString();
    }
}
