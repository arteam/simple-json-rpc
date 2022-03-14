package com.github.arteam.simplejsonrpc.server.simple.domain;

import java.util.Date;

/**
 * Date: 7/29/14
 * Time: 12:49 PM
 */
public record Player(String firstName, String lastName,
                     Team team, int number,
                     Position position, Date birthDate,
                     double capHit) {
}
