package com.github.arteam.dropwizard.json.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.github.arteam.dropwizard.json.rpc.domain.Player;
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

    private static ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    private static Map<String, RequestResponse> testData;

    private JsonRpcController rpcController = new JsonRpcController(mapper);
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
    public void testAddPlayer() throws Exception {
        RequestResponse requestResponse = testData.get("add_player");

        String textRequest = mapper.writeValueAsString(requestResponse.request);
        String actual = rpcController.handle(textRequest, teamService);
        Assert.assertEquals(requestResponse.response, mapper.readTree(actual));

        Player player = teamService.findByInitials("Kevin", "Shattenkirk");
        System.out.println(player);
    }

    @Test
    public void testFindPlayer() throws Exception {
        RequestResponse requestResponse = testData.get("find_player");

        String textRequest = mapper.writeValueAsString(requestResponse.request);
        String actual = rpcController.handle(textRequest, teamService);
        Assert.assertEquals(requestResponse.response, mapper.readTree(actual));
    }

    @Test
    public void testFind() throws Exception {
        RequestResponse requestResponse = testData.get("find");

        String textRequest = mapper.writeValueAsString(requestResponse.request);
        String actual = rpcController.handle(textRequest, teamService);
        Assert.assertEquals(requestResponse.response, mapper.readTree(actual));
    }
}
