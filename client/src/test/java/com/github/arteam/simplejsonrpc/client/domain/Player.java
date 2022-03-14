package com.github.arteam.simplejsonrpc.client.domain;

import java.util.Date;

public record Player(String firstName, String lastName,
                     Team team, int number,
                     Position position, Date birthDate,
                     double capHit) {
}
