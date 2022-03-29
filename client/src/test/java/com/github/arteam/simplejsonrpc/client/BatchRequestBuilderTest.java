package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.arteam.simplejsonrpc.client.domain.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Date: 10/12/14
 * Time: 9:38 PM
 */
public class BatchRequestBuilderTest {

    private static final TypeReference<Player> PLAYER_TYPE_REFERENCE = new TypeReference<>() {
    };
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private static Map<String, RequestResponse> requestsResponses;

    @BeforeAll
    public static void load() throws Exception {
        requestsResponses = mapper.readValue(BatchRequestBuilderTest.class.getResource("/batch_requests.json"),
                new TypeReference<>() {
                });
    }

    private JsonRpcClient initClient(String testName) {
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

    private static Map<String, String> stevenStamkos() {
        return Map.of("firstName", "Steven", "lastName", "Stamkos");
    }

    private static Map<String, String> jackAllen() {
        return Map.of("firstName", "Jack", "lastName", "Allen");
    }

    private static Map<String, String> vladimirSobotka() {
        return Map.of("firstName", "Vladimir", "lastName", "Sobotka");
    }

    private static void checkBatch(Map<String, Player> result) {
        assertThat(result.get("43121").firstName()).isEqualTo("Steven");
        assertThat(result.get("43121").lastName()).isEqualTo("Stamkos");
        assertThat(result.get("43122").firstName()).isEqualTo("Jack");
        assertThat(result.get("43122").lastName()).isEqualTo("Allen");
        assertThat(result.get("43123")).isNull();
    }

    @SuppressWarnings("unchecked")
    private static void checkUncheckedBatch(Map<String, ?> result) {
        assertThat(result.get("43121")).isExactlyInstanceOf(Player.class);
        assertThat(result.get("43122")).isExactlyInstanceOf(Player.class);
        checkBatch((Map<String, Player>) result);
    }

    @Test
    public void testBatchCommonType() {
        JsonRpcClient client = initClient("batch");
        Map<String, Player> result = client.createBatchRequest()
                .add("43121", "findByInitials", stevenStamkos())
                .add("43122", "findByInitials", jackAllen())
                .add("43123", "findByInitials", vladimirSobotka())
                .keysType(String.class)
                .returnType(Player.class)
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
                .add("43121", "findByInitials", stevenStamkos(), PLAYER_TYPE_REFERENCE)
                .add("43122", "findByInitials", jackAllen(), PLAYER_TYPE_REFERENCE)
                .add("43123", "findByInitials", vladimirSobotka(), PLAYER_TYPE_REFERENCE)
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
                .returnType(PLAYER_TYPE_REFERENCE)
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
                .add("43121", "findByInitials", new Object[]{"Steven", "Stamkos"}, PLAYER_TYPE_REFERENCE)
                .add("43122", "findByInitials", new Object[]{"Jack", "Allen"}, PLAYER_TYPE_REFERENCE)
                .add("43123", "findByInitials", new Object[]{"Vladimir", "Sobotka"}, PLAYER_TYPE_REFERENCE)
                .keysType(String.class)
                .execute();
        checkUncheckedBatch(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDifferentRequests() {
        JsonRpcClient client = initClient("different_requests");
        Map<Integer, ?> result = client.createBatchRequest()
                .add(12000, "isAlive", new HashMap<>(), Boolean.class)
                .add(12001, "findByInitials", new Object[]{"Kevin", "Shattenkirk"}, Player.class)
                .add(12002, "find_by_birth_year", Map.of("birth_year", 1990),
                        new TypeReference<List<Player>>() {
                        })
                .keysType(Integer.class)
                .execute();
        assertThat(result.get(12000)).isExactlyInstanceOf(Boolean.class);
        assertThat((Boolean) result.get(12000)).isTrue();
        assertThat(result.get(12001)).isExactlyInstanceOf(Player.class);
        assertThat(((Player) result.get(12001)).firstName()).isEqualTo("Kevin");
        assertThat(((Player) result.get(12001)).lastName()).isEqualTo("Shattenkirk");
        assertThat(result.get(12002)).isInstanceOf(List.class);
        assertThat((List<Player>) result.get(12002)).hasSize(3);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLongIds() {
        JsonRpcClient client = initClient("different_requests");
        Map<Long, ?> result = client.createBatchRequest()
                .add(12000L, "isAlive", new HashMap<>(), Boolean.class)
                .add(12001L, "findByInitials", new Object[]{"Kevin", "Shattenkirk"}, Player.class)
                .add(12002L, "find_by_birth_year", Map.of("birth_year", 1990),
                        new TypeReference<List<Player>>() {
                        })
                .keysType(Long.class)
                .execute();
        assertThat(result.get(12000L)).isExactlyInstanceOf(Boolean.class);
        assertThat((Boolean) result.get(12000L)).isTrue();
        assertThat(result.get(12001L)).isExactlyInstanceOf(Player.class);
        assertThat(((Player) result.get(12001L)).firstName()).isEqualTo("Kevin");
        assertThat(((Player) result.get(12001L)).lastName()).isEqualTo("Shattenkirk");
        assertThat(result.get(12002L)).isInstanceOf(List.class);
        assertThat((List<Player>) result.get(12002L)).hasSize(3);
    }

    @Test
    public void testBatchWithNotifications() {
        JsonRpcClient client = initClient("batch_with_notification");
        Map<Integer, ?> result = client.createBatchRequest()
                .add(1, "findByInitials", stevenStamkos(), Player.class)
                .add("updateCache")
                .add(2, "findByInitials", new Object[]{"Vladimir", "Sobotka"}, PLAYER_TYPE_REFERENCE)
                .keysType(Integer.class)
                .execute();
        assertThat(result.get(1)).isExactlyInstanceOf(Player.class);
        assertThat(((Player) result.get(1)).firstName()).isEqualTo("Steven");
        assertThat(((Player) result.get(1)).lastName()).isEqualTo("Stamkos");
        assertThat(result.get(3)).isNull();
    }

    @Test
    public void testAllNotifications() {
        JsonRpcClient client = initClient("all_notifications");
        Map<?, ?> result = client.createBatchRequest()
                .add("isAlive")
                .add("updateCache", Map.of("name", "assets"))
                .add("newSchedule", 0, 2, 0, 0, 0)
                .execute();
        assertThat(result).isEmpty();
    }
}
