package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.arteam.simplejsonrpc.client.domain.Player;
import com.github.arteam.simplejsonrpc.client.domain.Position;
import com.github.arteam.simplejsonrpc.client.domain.Team;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Date: 8/9/14
 * Time: 10:55 PM
 *
 * @author Artem Prigoda
 */
public class JsonRpcClientTest {

    private static Map<String, RequestResponse> requestsResponses;

    ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


    @BeforeClass
    public static void load() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        requestsResponses = mapper.readValue(JsonRpcClientTest.class.getResource("/client_test_data.json"),
                mapper.getTypeFactory().constructMapType(HashMap.class, String.class, RequestResponse.class));
    }

    @Test
    public void testAddPlayer() {
        final RequestResponse requestResponse = requestsResponses.get("add_player");
        JsonRpcClient client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                JsonNode requestNode = mapper.readTree(request);
                assertThat(requestNode).isEqualTo(requestResponse.request);
                return mapper.writeValueAsString(requestResponse.response);
            }
        }, mapper);
        Boolean result = client.createRequest()
                .id("asd671")
                .method("add")
                .param("player", new Player("Kevin", "Shattenkirk",
                        new Team("St. Louis Blues", "NHL"), 22, Position.DEFENDER,
                        ISODateTimeFormat.date().withZone(DateTimeZone.UTC).parseDateTime("1989-01-29").toDate(),
                        4.25))
                .responseType(Boolean.class)
                .execute();
        assertThat(result).isTrue();
    }


}
