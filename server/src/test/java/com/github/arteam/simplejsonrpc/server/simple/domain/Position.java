package com.github.arteam.simplejsonrpc.server.simple.domain;

/**
 * Date: 7/29/14
 * Time: 12:51 PM
 *
 * @author Artem Prigoda
 */
public enum Position {
    GOALTENDER("G"), DEFENDER("D"), RIGHT_WINGER("RW"), LEFT_WINGER("LW"), CENTER("C");

    private static final Position[] VALUES = values();

    private String code;

    private Position(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Position byCode(String code) {
        for (Position position : VALUES) {
            if (position.code.equalsIgnoreCase(code)) {
                return position;
            }
        }
        throw new IllegalStateException("Unale find Position by code=" + code);
    }
}
