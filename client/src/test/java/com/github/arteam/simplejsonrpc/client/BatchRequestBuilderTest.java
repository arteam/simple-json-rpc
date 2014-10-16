package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.github.arteam.simplejsonrpc.client.domain.Player;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

    private static Map<String, String> stevenStamkos() {
        return ImmutableMap.of("firstName", "Steven", "lastName", "Stamkos");
    }

    private static ImmutableMap<String, String> jackAllen() {
        return ImmutableMap.of("firstName", "Jack", "lastName", "Allen");
    }

    private static ImmutableMap<String, String> vladimirSobotka() {
        return ImmutableMap.of("firstName", "Vladimir", "lastName", "Sobotka");
    }

    private static void checkBatch(Map<String, Player> result) {
        assertThat(result.get("43121").getFirstName()).isEqualTo("Steven");
        assertThat(result.get("43121").getLastName()).isEqualTo("Stamkos");
        assertThat(result.get("43122").getFirstName()).isEqualTo("Jack");
        assertThat(result.get("43122").getLastName()).isEqualTo("Allen");
        assertThat(result.get("43123")).isNull();
    }

    @SuppressWarnings("unchecked")
    private static void checkUncheckedBatch(Map<String, ?> result) {
        assertThat(result.get("43121")).isExactlyInstanceOf(Player.class);
        assertThat(result.get("43122")).isExactlyInstanceOf(Player.class);
        checkBatch((Map<String, Player>) result);
    }

    private static TypeReference<Player> playerTypeReference() {
        return new TypeReference<Player>() {
        };
    }

    @Test
    public void testBatchCommonType() {
        JsonRpcClient client = initClient("batch");
        Map<String, Player> result = client.createBatchRequest()
                .add("43121", "findByInitials", stevenStamkos())
                .add("43122", "findByInitials", jackAllen())
                .add("43123", "findByInitials", vladimirSobotka())
                .keysType(String.class)
                .valuesType(Player.class)
                .execute();
        checkBatch(result);
    }

    @Test
    public void testBatchDetailedTypes() {
        JsonRpcClient client = initClient("batch");
        Map<String, ?> result = client.createBatchRequest()
                .add("43121", "findByInitials", stevenStamkos(), Player.class)
                .add("43122", "findByInitials", jackAllen(), Player.class)
                .add("43123", "findByInitials", vladimirSobotka(), Player.class)
                .keysType(String.class)
                .execute();
        checkUncheckedBatch(result);
    }

    @Test
    public void testBatchTypeReferences() {
        JsonRpcClient client = initClient("batch");
        Map<String, ?> result = client.createBatchRequest()
                .add("43121", "findByInitials", stevenStamkos(), playerTypeReference())
                .add("43122", "findByInitials", jackAllen(), playerTypeReference())
                .add("43123", "findByInitials", vladimirSobotka(), playerTypeReference())
                .keysType(String.class)
                .execute();
        checkUncheckedBatch(result);
    }

    @Test
    public void testBatchArrayRequests() {
        JsonRpcClient client = initClient("batch_array");
        Map<String, Player> result = client.createBatchRequest()
                .add("43121", "findByInitials", "Steven", "Stamkos")
                .add("43122", "findByInitials", "Jack", "Allen")
                .add("43123", "findByInitials", "Vladimir", "Sobotka")
                .keysType(String.class)
                .valuesType(Player.class)
                .execute();
        checkBatch(result);
    }

    @Test
    public void testBatchArrayRequestsWithDetailedTypes() {
        JsonRpcClient client = initClient("batch_array");
        Map<String, ?> result = client.createBatchRequest()
                .add("43121", "findByInitials", new Object[]{"Steven", "Stamkos"}, Player.class)
                .add("43122", "findByInitials", new Object[]{"Jack", "Allen"}, Player.class)
                .add("43123", "findByInitials", new Object[]{"Vladimir", "Sobotka"}, Player.class)
                .keysType(String.class)
                .execute();
        checkUncheckedBatch(result);
    }

    @Test
    public void testBatchArrayRequestsWithTypeReferences() {
        JsonRpcClient client = initClient("batch_array");
        Map<String, ?> result = client.createBatchRequest()
                .add("43121", "findByInitials", new Object[]{"Steven", "Stamkos"}, playerTypeReference())
                .add("43122", "findByInitials", new Object[]{"Jack", "Allen"}, playerTypeReference())
                .add("43123", "findByInitials", new Object[]{"Vladimir", "Sobotka"}, playerTypeReference())
                .keysType(String.class)
                .execute();
        checkUncheckedBatch(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDifferentRequests() {
        JsonRpcClient client = initClient("different_requests");
        Map<Integer, ?> result = client.createBatchRequest()
                .add(12000, "isAlive", new HashMap<String, Object>(), Boolean.class)
                .add(12001, "findByInitials", new Object[]{"Kevin", "Shattenkirk"}, Player.class)
                .add(12002, "find_by_birth_year", ImmutableMap.of("birth_year", 1990), new TypeReference<List<Player>>(){})
                .keysType(Integer.class)
                .execute();
        assertThat(result.get(12000)).isExactlyInstanceOf(Boolean.class);
        assertThat((Boolean) result.get(12000)).isTrue();
        assertThat(result.get(12001)).isExactlyInstanceOf(Player.class);
        assertThat(((Player) result.get(12001)).getFirstName()).isEqualTo("Kevin");
        assertThat(((Player) result.get(12001)).getLastName()).isEqualTo("Shattenkirk");
        assertThat(result.get(12002)).isInstanceOf(List.class);
        assertThat((List<Player>) result.get(12002)).hasSize(3);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLongIds() {
        JsonRpcClient client = initClient("different_requests");
        Map<Long, ?> result = client.createBatchRequest()
                .add(12000L, "isAlive", new HashMap<String, Object>(), Boolean.class)
                .add(12001L, "findByInitials", new Object[]{"Kevin", "Shattenkirk"}, Player.class)
                .add(12002L, "find_by_birth_year", ImmutableMap.of("birth_year", 1990), new TypeReference<List<Player>>(){})
                .keysType(Long.class)
                .execute();
        assertThat(result.get(12000L)).isExactlyInstanceOf(Boolean.class);
        assertThat((Boolean) result.get(12000L)).isTrue();
        assertThat(result.get(12001L)).isExactlyInstanceOf(Player.class);
        assertThat(((Player) result.get(12001L)).getFirstName()).isEqualTo("Kevin");
        assertThat(((Player) result.get(12001L)).getLastName()).isEqualTo("Shattenkirk");
        assertThat(result.get(12002L)).isInstanceOf(List.class);
        assertThat((List<Player>) result.get(12002L)).hasSize(3);
    }

    @Test
    public void testBatchWithNotifications() {
        JsonRpcClient client = initClient("batch_with_notification");
        Map<Integer, ?> result = client.createBatchRequest()
                .add(1, "findByInitials", stevenStamkos(), Player.class)
                .add("updateCache")
                .add(2, "findByInitials", new Object[]{"Vladimir", "Sobotka"}, playerTypeReference())
                .keysType(Integer.class)
                .execute();
        assertThat(result.get(1)).isExactlyInstanceOf(Player.class);
        assertThat(((Player) result.get(1)).getFirstName()).isEqualTo("Steven");
        assertThat(((Player) result.get(1)).getLastName()).isEqualTo("Stamkos");
        assertThat(result.get(3)).isNull();
    }

    @Test
    public void testAllNotifications() {
        JsonRpcClient client = initClient("all_notifications");
        Map<?, ?> result = client.createBatchRequest()
                .add("isAlive")
                .add("updateCache", ImmutableMap.of("name", "assets"))
                .add("newSchedule", 0, 2, 0, 0, 0)
                .execute();
        assertThat(result).isEmpty();
    }
}
