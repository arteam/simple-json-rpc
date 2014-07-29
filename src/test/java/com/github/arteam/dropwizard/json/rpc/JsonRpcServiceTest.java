package com.github.arteam.dropwizard.json.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.github.arteam.dropwizard.json.rpc.protocol.controller.JsonRpcController;
import com.github.arteam.dropwizard.json.rpc.service.TeamService;
import com.github.arteam.dropwizard.json.rpc.util.RequestResponse;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

/**
 * Date: 7/28/14
 * Time: 10:29 PM
 *
 * @author Artem Prigoda
 */
public class JsonRpcServiceTest {

    private static ObjectMapper mapper = new ObjectMapper();
    private static Map<String, RequestResponse> testData;

    private JsonRpcController rpcController = new JsonRpcController();
    private TeamService teamService = new TeamService();

    @BeforeClass
    public static void init() throws Exception {
        testData = mapper.readValue(Resources.toString(
                Resources.getResource(JsonRpcServiceTest.class, "/test_data.json"),
                Charsets.UTF_8), MapType.construct(Map.class,
                SimpleType.construct(String.class),
                SimpleType.construct(RequestResponse.class)));
    }

    @Test
    public void test() throws Exception {
        RequestResponse requestResponse = testData.get("add_player");

        String textRequest = mapper.writeValueAsString(requestResponse.request);
        String actual = rpcController.handle(textRequest, teamService);
        Assert.assertEquals(requestResponse.response, mapper.readTree(actual));
    }
}
