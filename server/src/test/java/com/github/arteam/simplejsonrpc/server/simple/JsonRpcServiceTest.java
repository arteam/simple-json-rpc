package com.github.arteam.simplejsonrpc.server.simple;

import com.github.arteam.simplejsonrpc.server.JsonRpcServer;
import com.github.arteam.simplejsonrpc.server.simple.domain.Position;
import com.github.arteam.simplejsonrpc.server.simple.service.TeamService;
import com.github.arteam.simplejsonrpc.server.simple.util.RequestResponse;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.joda.time.format.DateTimeFormat;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Date: 7/28/14
 * Time: 10:29 PM
 * Tests typical patterns of a JSON-RPC interaction
 *
 * @author Artem Prigoda
 */
public class JsonRpcServiceTest {

    private static Gson userMapper = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                @Override
                public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                            .withZoneUTC()
                            .print(src.getTime()));
                }
            })
            .registerTypeAdapter(Position.class, new TypeAdapter<Position>() {
                @Override
                public void write(JsonWriter out, Position value) throws IOException {
                    out.value(value.getCode());
                }

                @Override
                public Position read(JsonReader in) throws IOException {
                    return Position.byCode(in.nextString());
                }
            })
            .registerTypeAdapter(Optional.class, new JsonDeserializer<Optional<?>>() {
                @Override
                public Optional<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    if (json == null || json.isJsonNull()) {
                        return Optional.absent();
                    }
                    Type actualType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
                    return Optional.of(context.deserialize(json, actualType));
                }
            })
            .create();
    //.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    private static Map<String, RequestResponse> testData;

    private static JsonRpcServer rpcServer = JsonRpcServer.withGson(userMapper);
    private static TeamService teamService = new TeamService();

    @BeforeClass
    public static void init() throws Exception {
        //userMapper.registerModule(new GuavaModule());
        testData = userMapper.fromJson(
                Resources.toString(JsonRpcServiceTest.class.getResource("/test_data.json"), Charsets.UTF_8),
                new TypeToken<Map<String, RequestResponse>>() {
                }.getType());
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
            String textRequest = userMapper.toJson(requestResponse.request);

            String actual = rpcServer.handle(textRequest, teamService);
            if (!actual.isEmpty()) {
                assertThat(userMapper.fromJson(actual, JsonElement.class)).isEqualTo(requestResponse.response);
            } else {
                assertThat(actual).isEqualTo(requestResponse.response.getAsString());
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
