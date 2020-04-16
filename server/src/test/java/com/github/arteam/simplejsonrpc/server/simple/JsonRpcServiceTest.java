package com.github.arteam.simplejsonrpc.server.simple;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.arteam.simplejsonrpc.server.JsonRpcServer;
import com.github.arteam.simplejsonrpc.server.simple.service.TeamService;
import com.github.arteam.simplejsonrpc.server.simple.util.RequestResponse;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Date: 7/28/14
 * Time: 10:29 PM
 * Tests typical patterns of a JSON-RPC interaction
 */
public class JsonRpcServiceTest {

    private static ObjectMapper userMapper = new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    private static Map<String, RequestResponse> testData;

    private static JsonRpcServer rpcServer = JsonRpcServer.withMapper(userMapper);
    private static TeamService teamService = new TeamService();

    @BeforeAll
    public static void init() throws Exception {
        userMapper.registerModule(new GuavaModule());
        userMapper.registerModule(new Jdk8Module());
        testData = new ObjectMapper().readValue(Resources.toString(JsonRpcServiceTest.class.getResource("/test_data.json"), Charsets.UTF_8),
                                                TypeFactory.defaultInstance()
                                                           .constructMapType(Map.class,
                                                                             TypeFactory.defaultInstance().constructType(String.class),
                                                                             TypeFactory.defaultInstance().constructType(RequestResponse.class)));
    }

    /**
     * Tests adding of a complex object
     */
    @Test
    public void testAddPlayer() {
        test("add_player");
        test("find_shattenkirk");
    }

    /**
     * Tests usual case (request and complex response)
     */
    @Test
    public void testFindPlayer() {
        test("find_player");
    }

    /**
     * Tests null as a result
     */
    @Test
    public void testPlayerIsNotFound() {
        test("player_is_not_found");
    }

    /**
     * Tests params are set as array
     */
    @Test
    public void testFindPlayerWithArrayParams() {
        test("find_player_array");
    }

    /**
     * Tests optional fields
     */
    @Test
    public void testFind() {
        test("find");
    }

    /**
     * Tests overridden method name and response as a list of objects
     */
    @Test
    public void testFindByBirthYear() {
        test("findByBirthYear");
    }

    /**
     * Tests optional fields in array params
     */
    @Test
    public void testFindWithArrayNullParams() {
        test("find_array_null_params");
    }

    /**
     * Tests calling a method from super-class and method without parameters
     */
    @Test
    public void testIsAlive() {
        test("isAlive");
    }

    /**
     * Tests a notification request
     */
    @Test
    public void testNotification() {
        test("notification");
    }

    /**
     * Tests a batch request
     */
    @Test
    public void testBatch() {
        test("batch");
    }

    /**
     * Tests a mixed request
     */
    @Test
    public void testBatchWithNotification() {
        test("batchWithNotification");
    }

    /**
     * Tests passing list as a parameter
     */
    @Test
    public void testFindPlayersByNames() {
        test("findPlayersByFirstNames");
    }

    @Test
    public void testFindPlayersByNumbers() {
        test("findPlayersByNumbers");
    }

    @Test
    public void testGetContractSums() {
        test("getContractSums");
    }

    @Test
    public void testGenericFindPlayersByNumbers() {
        test("genericFindPlayersByNumbers");
    }

    private void test(String testName) {
        try {
            RequestResponse requestResponse = testData.get(testName);
            String textRequest = userMapper.writeValueAsString(requestResponse.request);

            String actual = rpcServer.handle(textRequest, teamService);
            if (!actual.isEmpty()) {
                assertThat(userMapper.readTree(actual)).isEqualTo(requestResponse.response);
            } else {
                assertThat(actual).isEqualTo(requestResponse.response.asText());
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
