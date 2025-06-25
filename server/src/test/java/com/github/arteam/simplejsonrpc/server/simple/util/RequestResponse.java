package com.github.arteam.simplejsonrpc.server.simple.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * Date: 7/28/14
 * Time: 11:26 PM
 */
public class RequestResponse {
    private JsonNode request;
    private JsonNode response;

    public RequestResponse(){}

    public RequestResponse(JsonNode request, JsonNode response) {
        this.request = request;
        this.response = response;
    }

    @JsonProperty
    public JsonNode request() {
        return request;
    }

    @JsonProperty
    public JsonNode response() {
        return response;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RequestResponse) obj;
        return Objects.equals(this.request, that.request) &&
                Objects.equals(this.response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request, response);
    }

    @Override
    public String toString() {
        return "RequestResponse[" +
                "request=" + request + ", " +
                "response=" + response + ']';
    }

}
