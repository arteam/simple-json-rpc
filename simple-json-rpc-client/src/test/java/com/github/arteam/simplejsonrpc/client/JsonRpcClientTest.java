package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.arteam.simplejsonrpc.client.domain.Player;
import com.github.arteam.simplejsonrpc.client.domain.Position;
import com.github.arteam.simplejsonrpc.client.domain.Team;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

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
    public void testAddPlayer() {
        JsonRpcClient client = initClient("add_player");
        Boolean result = client.createRequest()
                .id("asd671")
                .method("add")
                .param("player", new Player("Kevin", "Shattenkirk",
                        new Team("St. Louis Blues", "NHL"), 22, Position.DEFENDER,
                        ISODateTimeFormat.date().withZone(DateTimeZone.UTC).parseDateTime("1989-01-29").toDate(),
                        4.25))
                .returnAs(Boolean.class)
                .execute();
        assertThat(result).isTrue();
    }

    @Test
    public void findPlayerByInitials() {
        JsonRpcClient client = initClient("find_player");
        Player player = client.createRequest()
                .method("findByInitials")
                .id(43121)
                .param("firstName", "Steven")
                .param("lastName", "Stamkos")
                .returnAs(Player.class)
                .execute();
        assertThat(player).isNotNull();
        assertThat(player.getFirstName()).isEqualTo("Steven");
        assertThat(player.getLastName()).isEqualTo("Stamkos");
    }

    @Test
    public void testPlayerIsNotFound() {
        JsonRpcClient client = initClient("player_is_not_found");
        Player player = client.createRequest()
                .method("findByInitials")
                .id(4111L)
                .param("firstName", "Vladimir")
                .param("lastName", "Sobotka")
                .returnAs(Player.class)
                .execute();
        assertThat(player).isNull();
    }

    @Test
    public void testFindArray() {
        JsonRpcClient client = initClient("find_player_array");
        Player player = client.createRequest()
                .method("findByInitials")
                .id("dsfs1214")
                .params("Ben", "Bishop")
                .returnAs(Player.class)
                .execute();
        assertThat(player).isNotNull();
        assertThat(player.getFirstName()).isEqualTo("Ben");
        assertThat(player.getLastName()).isEqualTo("Bishop");
    }

    @Test
    public void testReturnList() {
        JsonRpcClient client = initClient("findByBirthYear");
        List<Player> players = client.createRequest()
                .method("find_by_birth_year")
                .id(5621)
                .param("birth_year", 1990)
                .returnAsList(Player.class)
                .execute();
        assertThat(players).isNotNull();
        assertThat(players).hasSize(3);
        assertThat(players.get(0).getLastName()).isEqualTo("Allen");
        assertThat(players.get(1).getLastName()).isEqualTo("Stamkos");
        assertThat(players.get(2).getLastName()).isEqualTo("Hedman");
    }

    @Test
    public void testReturnSet() {
        JsonRpcClient client = initClient("findByBirthYear");
        Set<Player> players = client.createRequest()
                .method("find_by_birth_year")
                .id(5621)
                .param("birth_year", 1990)
                .returnAsSet(Player.class)
                .execute();
        assertThat(players).isNotNull();
        assertThat(players).hasSize(3);
        List<String> lastNames = Lists.newArrayList();
        for (Player player : players) {
            lastNames.add(player.getLastName());
        }
        assertThat(lastNames).containsOnly("Allen", "Stamkos", "Hedman");
    }

    @Test
    public void testReturnArray() {
        JsonRpcClient client = initClient("findByBirthYear");
        Player[] players = client.createRequest()
                .method("find_by_birth_year")
                .id(5621)
                .param("birth_year", 1990)
                .returnAsArray(Player.class)
                .execute();
        assertThat(players).isNotNull();
        assertThat(players).hasSize(3);
        assertThat(players[0].getLastName()).isEqualTo("Allen");
        assertThat(players[1].getLastName()).isEqualTo("Stamkos");
        assertThat(players[2].getLastName()).isEqualTo("Hedman");
    }

    @Test
    public void testReturnCollection() {
        JsonRpcClient client = initClient("findByBirthYear");
        Deque<Player> players = (Deque<Player>) client.createRequest()
                .method("find_by_birth_year")
                .id(5621)
                .param("birth_year", 1990)
                .returnAsCollection(Deque.class, Player.class)
                .execute();
        assertThat(players).isNotNull();
        assertThat(players).hasSize(3);
        assertThat(players.pop().getLastName()).isEqualTo("Allen");
        assertThat(players.pop().getLastName()).isEqualTo("Stamkos");
        assertThat(players.pop().getLastName()).isEqualTo("Hedman");
    }

}
