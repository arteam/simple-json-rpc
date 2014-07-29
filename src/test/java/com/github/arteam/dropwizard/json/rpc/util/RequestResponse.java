package com.github.arteam.dropwizard.json.rpc.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Date: 7/28/14
 * Time: 11:26 PM
 *
 * @author Artem Prigoda
 */
public class RequestResponse {

    @JsonProperty
    public ObjectNode request;

    @JsonProperty
    public ObjectNode response;
}
