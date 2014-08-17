package com.github.arteam.simplejsonrpc.server.simple.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Date: 7/28/14
 * Time: 11:26 PM
 *
 * @author Artem Prigoda
 */
public class RequestResponse {

    @JsonProperty
    public JsonNode request;

    @JsonProperty
    public JsonNode response;
}
