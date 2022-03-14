package com.github.arteam.simplejsonrpc.server.simple.util;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Date: 7/28/14
 * Time: 11:26 PM
 */
public record RequestResponse(JsonNode request, JsonNode response) {
}
