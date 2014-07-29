package com.github.arteam.dropwizard.json.rpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arteam.dropwizard.json.rpc.protocol.controller.JsonRpcController;
import com.github.arteam.dropwizard.json.rpc.service.TeamService;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Date: 7/29/14
 * Time: 7:58 PM
 *
 * @author Artem Prigoda
 */
public class JsonRpcErrorsTest {

    private JsonRpcController rpcController = new JsonRpcController();
    private TeamService teamService = new TeamService();

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static String requestFile(String name) {
        try {
            return Resources.toString(JsonRpcErrorsTest.class.getResource("/error/request/" + name), Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String responseFile(String name) {
        try {
            return Resources.toString(JsonRpcErrorsTest.class.getResource("/error/response/" + name), Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    private static JsonNode json(String text) {
        try {
            return objectMapper.readTree(text);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void testBadJson() {
        String response = rpcController.handle(requestFile("bad_json.json"), teamService);
        assertThat(json(response)).isEqualTo(json(responseFile("parse_error.json")));
    }

    @Test
    public void testNotJsonRpc() {
        String response = rpcController.handle(requestFile("not_json_rpc.json"), teamService);
        assertThat(json(response)).isEqualTo(json(responseFile("invalid_request.json")));
    }

    @Test
    public void testNoVersion() {
        String response = rpcController.handle(requestFile("no_version.json"), teamService);
        assertThat(json(response)).isEqualTo(json(responseFile("invalid_request_with_id.json")));
    }

    @Test
    public void testNoMethod() {
        String response = rpcController.handle(requestFile("no_method.json"), teamService);
        assertThat(json(response)).isEqualTo(json(responseFile("invalid_request_with_id.json")));
    }

    @Test
    public void testNoParams() {
        String response = rpcController.handle(requestFile("no_params.json"), teamService);
        assertThat(json(response)).isEqualTo(json(responseFile("invalid_request.json")));
    }

    @Test
    public void testBadMethodType() {
        String response = rpcController.handle(requestFile("bad_method_type.json"), teamService);
        assertThat(json(response)).isEqualTo(json(responseFile("invalid_request.json")));
    }

    @Test
    public void testBadParamsType() {
        String response = rpcController.handle(requestFile("bad_params_type.json"), teamService);
        assertThat(json(response)).isEqualTo(json(responseFile("invalid_request.json")));
    }

    @Test
    public void testBadVersion() {
        String response = rpcController.handle(requestFile("bad_version.json"), teamService);
        assertThat(json(response)).isEqualTo(json(responseFile("invalid_request.json")));
    }

    @Test
    public void testBadId() {
        String response = rpcController.handle(requestFile("bad_id.json"), teamService);
        assertThat(json(response)).isEqualTo(json(responseFile("invalid_request.json")));
    }

    @Test
    public void testNotJsonRpc20() {
        String response = rpcController.handle(requestFile("not_json_rpc_20.json"), teamService);
        assertThat(json(response)).isEqualTo(json(responseFile("invalid_request_with_id.json")));
    }
}
