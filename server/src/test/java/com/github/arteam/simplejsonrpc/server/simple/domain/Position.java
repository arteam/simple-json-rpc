package com.github.arteam.simplejsonrpc.server.simple.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Date: 7/29/14
 * Time: 12:51 PM
 */
public enum Position {
    GOALTENDER("G"), DEFENDER("D"), RIGHT_WINGER("RW"), LEFT_WINGER("LW"), CENTER("C");

    private static final Position[] VALUES = values();

    private final String code;

    Position(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static Position byCode(String code) {
        for (Position position : VALUES) {
            if (position.code.equalsIgnoreCase(code)) {
                return position;
            }
        }
        throw new IllegalStateException("Unale find Position by code=" + code);
    }
}
