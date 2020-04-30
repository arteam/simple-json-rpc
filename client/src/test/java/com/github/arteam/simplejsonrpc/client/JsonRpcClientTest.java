package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.arteam.simplejsonrpc.client.domain.Player;
import com.github.arteam.simplejsonrpc.client.domain.Position;
import com.github.arteam.simplejsonrpc.client.domain.Team;
import com.github.arteam.simplejsonrpc.client.exception.JsonRpcException;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Date: 8/9/14
 * Time: 10:55 PM
 */
public class JsonRpcClientTest extends BaseClientTest {

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
                .executeNullable();
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

    @Test
    public void testNoParams() {
        JsonRpcClient client = initClient("getPlayers");
        List<Player> players = client.createRequest()
                .method("getPlayers")
                .id(1000)
                .returnAsList(Player.class)
                .execute();
        assertThat(players).isNotNull();
        assertThat(players).hasSize(3);
        assertThat(players.get(0).getLastName()).isEqualTo("Bishop");
        assertThat(players.get(1).getLastName()).isEqualTo("Tarasenko");
        assertThat(players.get(2).getLastName()).isEqualTo("Bouwmeester");
    }

    @Test
    public void testMap() {
        Map<String, Integer> contractLengths = new LinkedHashMap<String, Integer>() {{
            put("Backes", 4);
            put("Tarasenko", 3);
            put("Allen", 2);
            put("Bouwmeester", 5);
            put("Stamkos", 8);
            put("Callahan", 3);
            put("Bishop", 4);
            put("Hedman", 2);
        }};
        JsonRpcClient client = initClient("getContractSums");
        Map<String, Double> contractSums = client.createRequest()
                .method("getContractSums")
                .id(97555)
                .param("contractLengths", contractLengths)
                .returnAsMap(LinkedHashMap.class, Double.class)
                .execute();
        assertThat(contractSums).isExactlyInstanceOf(LinkedHashMap.class);
        assertThat(contractSums).isEqualTo(new HashMap<String, Double>() {{
            put("Backes", 18.0);
            put("Tarasenko", 2.7);
            put("Allen", 1.0);
            put("Bouwmeester", 27.0);
            put("Stamkos", 60.0);
            put("Callahan", 17.4);
            put("Bishop", 9.2);
            put("Hedman", 8.0);
        }});
    }

    @Test
    public void testOptional() {
        JsonRpcClient client = initClient("player_is_not_found");
        Optional<Player> optionalPlayer = client.createRequest()
                .method("findByInitials")
                .id(4111L)
                .param("firstName", "Vladimir")
                .param("lastName", "Sobotka")
                .returnAs(new TypeReference<Optional<Player>>() {
                })
                .execute();
        assertThat(optionalPlayer.isPresent()).isFalse();
    }

    @Test
    public void testJsonRpcError() {
        JsonRpcClient client = initClient("methodNotFound");
        try {
            client.createRequest()
                    .method("getPlayer")
                    .id(1001)
                    .returnAs(Player.class)
                    .execute();
            Assertions.fail();
        } catch (JsonRpcException e) {
            e.printStackTrace();
            ErrorMessage errorMessage = e.getErrorMessage();
            assertThat(errorMessage.getCode()).isEqualTo(-32601);
            assertThat(errorMessage.getMessage()).isEqualTo("Method not found");
        }
    }
    
    @Test
    public void testJsonRpcErrorWithDataAttribute() {
        JsonRpcClient client = initClient("errorWithDataAttribute");
        try {
            client.createRequest()
                    .method("getErrorWithData")
                    .id(1002)
                    .returnAs(Player.class)
                    .execute();
            Assertions.fail();
        } catch (JsonRpcException e) {
            e.printStackTrace();
            ErrorMessage errorMessage = e.getErrorMessage();
            assertThat(errorMessage.getCode()).isEqualTo(-32000);
            assertThat(errorMessage.getMessage()).isEqualTo("This is an error with data attribute");
            assertThat(errorMessage.getData()).isEqualTo(JsonNodeFactory.instance.textNode("Error data"));
        }
    }
    
    @Test
    public void testJsonRpcErrorWithNullDataAttribute() {
        JsonRpcClient client = initClient("errorWithNullDataAttribute");
        try {
            client.createRequest()
                    .method("getErrorWithNullData")
                    .id(1003)
                    .returnAs(Player.class)
                    .execute();
            Assertions.fail();
        } catch (JsonRpcException e) {
            e.printStackTrace();
            ErrorMessage errorMessage = e.getErrorMessage();
            assertThat(errorMessage.getCode()).isEqualTo(-32000);
            assertThat(errorMessage.getMessage()).isEqualTo("This is an error with null data attribute");
            assertThat(errorMessage.getData()).isEqualTo(JsonNodeFactory.instance.nullNode());
        }
    }
    
    @Test
    public void testJsonRpcErrorWithStructuredDataAttribute() {
        JsonRpcClient client = initClient("errorWithStructuredDataAttribute");
        try {
            client.createRequest()
                    .method("getErrorWithStructuredData")
                    .id(1003)
                    .returnAs(Player.class)
                    .execute();
            Assertions.fail();
        } catch (JsonRpcException e) {
            e.printStackTrace();
            ErrorMessage errorMessage = e.getErrorMessage();
            assertThat(errorMessage.getCode()).isEqualTo(-32000);
            assertThat(errorMessage.getMessage()).isEqualTo("This is an error with structured data attribute");
            assertThat(errorMessage.getData()).isNotNull();
        }
    }
}
