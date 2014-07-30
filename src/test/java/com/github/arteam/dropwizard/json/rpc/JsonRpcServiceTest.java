package com.github.arteam.dropwizard.json.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.github.arteam.dropwizard.json.rpc.protocol.controller.JsonRpcServer;
import com.github.arteam.dropwizard.json.rpc.service.TeamService;
import com.github.arteam.dropwizard.json.rpc.util.RequestResponse;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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

    private JsonRpcServer rpcController = new JsonRpcServer(mapper);
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
    public void testAddPlayer() {
        test("add_player");
        test("find_shattenkirk");
    }

    @Test
    public void testFindPlayer() {
        test("find_player");
    }

    @Test
    public void testPlayerIsNotFound() {
        test("player_is_not_found");
    }

    @Test
    public void testFindPlayerWithArrayParams() {
        test("find_player_array");
    }

    @Test
    public void testFind() {
        test("find");
    }

    @Test
    public void testFindByBirthYear() {
        test("findByBirthYear");
    }

    @Test
    public void testFindWithArrayNullParams() {
        test("find_array_null_params");
    }

    private void test(String testName) {
        try {
            RequestResponse requestResponse = testData.get(testName);
            String textRequest = mapper.writeValueAsString(requestResponse.request);

            String actual = rpcController.handle(textRequest, teamService);
            assertThat(requestResponse.response).isEqualTo(mapper.readTree(actual));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
