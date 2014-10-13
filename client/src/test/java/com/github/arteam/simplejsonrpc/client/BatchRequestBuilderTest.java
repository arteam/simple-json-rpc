package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.github.arteam.simplejsonrpc.client.domain.Player;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Date: 10/12/14
 * Time: 9:38 PM
 *
 * @author Artem Prigoda
 */
public class BatchRequestBuilderTest {

    private static Map<String, RequestResponse> requestsResponses;

    ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .registerModule(new GuavaModule());


    @BeforeClass
    public static void load() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        requestsResponses = mapper.readValue(BatchRequestBuilderTest.class.getResource("/batch_requests.json"),
                mapper.getTypeFactory().constructMapType(HashMap.class, String.class, RequestResponse.class));
    }

    private JsonRpcClient initClient(String testName) {
        final RequestResponse requestResponse = requestsResponses.get(testName);
        return new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                System.out.println(request);
                JsonNode requestNode = mapper.readTree(request);
                assertThat(requestNode).isEqualTo(requestResponse.request);
                String response = mapper.writeValueAsString(requestResponse.response);
                System.out.println(response);
                return response;
            }
        }, mapper);
    }

    @Test
    public void test() {
        JsonRpcClient client = initClient("batch");
        Map<String, Player> result = client.createBatchRequest()
                .add("43121", "findByInitials", ImmutableMap.of("firstName", "Steven", "lastName", "Stamkos"))
                .add("43122", "findByInitials", ImmutableMap.of("firstName", "Jack", "lastName", "Allen"))
                .add("43123", "findByInitials", ImmutableMap.of("firstName", "Vladimir", "lastName", "Sobotka"))
                .keysType(String.class)
                .valuesType(Player.class)
                .execute();
        Assertions.assertThat(result.get("43121").getFirstName()).isEqualTo("Steven");
        Assertions.assertThat(result.get("43121").getLastName()).isEqualTo("Stamkos");
        Assertions.assertThat(result.get("43122").getFirstName()).isEqualTo("Jack");
        Assertions.assertThat(result.get("43122").getLastName()).isEqualTo("Allen");
        Assertions.assertThat(result.get("43123")).isNull();
    }
}
