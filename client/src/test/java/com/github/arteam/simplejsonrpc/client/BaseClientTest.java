package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Date: 24.08.14
 * Time: 18:25
 */
public class BaseClientTest {

    private static Map<String, RequestResponse> requestsResponses;

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .registerModule(new Jdk8Module());

    @BeforeAll
    public static void load() throws Exception {
        requestsResponses = new ObjectMapper()
                .readValue(BaseClientTest.class.getResource("/client_test_data.json"), new TypeReference<>() {
                });
    }

    protected JsonRpcClient initClient(String testName) {
        final RequestResponse requestResponse = requestsResponses.get(testName);
        return new JsonRpcClient(request -> {
            System.out.println(request);
            JsonNode requestNode = mapper.readTree(request);
            assertThat(requestNode).isEqualTo(requestResponse.request);
            String response = mapper.writeValueAsString(requestResponse.response);
            System.out.println(response);
            return response;
        }, mapper);
    }

    protected JsonRpcClient fakeClient() {
        return new JsonRpcClient(request -> "", mapper);
    }

}
