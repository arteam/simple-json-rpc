package com.github.arteam.simplejsonrpc.server.simple.domain;

import com.google.common.base.Objects;
import com.google.common.base.Objects;


/**
 * Date: 7/29/14
 * Time: 2:07 PM
 *
 * @author Artem Prigoda
 */
public class Team {

    private final String name;

    private final String league;

    public Team(String name, String league) {
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
