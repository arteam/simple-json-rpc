package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Date: 8/9/14
 * Time: 10:59 PM
 */
class RequestResponse {

    @JsonProperty
    public JsonNode request;

    @JsonProperty
    public JsonNode response;
}
