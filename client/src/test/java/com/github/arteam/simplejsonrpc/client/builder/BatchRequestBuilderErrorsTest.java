package com.github.arteam.simplejsonrpc.client.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.github.arteam.simplejsonrpc.client.JsonRpcClient;
import com.github.arteam.simplejsonrpc.client.domain.Player;
import com.github.arteam.simplejsonrpc.client.exception.JsonRpcBatchException;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

/**
 * Date: 10/23/14
 * Time: 11:55 PM
 */
public class BatchRequestBuilderErrorsTest {

    final JsonRpcClient client = new JsonRpcClient(request -> {
        System.out.println(request);
        return """
                [{
                    "jsonrpc": "2.0",
                    "id": 1,
                    "result": {
                        "firstName": "Steven",
                        "lastName": "Stamkos",
                        "team": {
                            "name": "Tampa Bay Lightning",
                            "league": "NHL"
                        },
                        "number": 91,
                        "position": "C",
                        "birthDate": "1990-02-07T00:00:00.000+00:00",
                        "capHit": 7.5
                    }
                }]
                """;
    });

    @Test
    public void testRequestsAreEmpty() {
        assertThatIllegalArgumentException().isThrownBy(() -> client.createBatchRequest().execute())
                .withMessage("Requests are not set");
    }

    @Test
    public void testRequestWithoutReturnType() {
        assertThatIllegalArgumentException().isThrownBy(() -> client.createBatchRequest()
                        .add(1L, "findPlayer", "Steven", "Stamkos")
                        .execute())
                .withMessage("Return type isn't specified for request with id='1'");
    }

    @Test
    public void testBothSingleAndGlobalResponseTypeAreSet() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> client.createBatchRequest()
                        .add(1L, "findPlayer", new Object[]{"Steven", "Stamkos"}, Player.class)
                        .returnType(Player.class)
                        .execute())
                .withMessage("Common and detailed configurations of return types shouldn't be mixed");
    }

    @Test
    public void testBadId() {
        assertThatIllegalArgumentException().isThrownBy(() -> {
            BatchRequestBuilder<?, ?> batchRequest = client.createBatchRequest();
            batchRequest.getRequests()
                    .add(batchRequest.request(BooleanNode.TRUE, "findPlayer",
                            new ObjectMapper().createArrayNode().add("Steven").add("Stamkos")));
            batchRequest.returnType(Player.class).execute();
        }).withMessage("Wrong id=true");
    }

    @Test
    public void tesKeyIdIsNotExpectedType() {
        assertThatIllegalArgumentException().isThrownBy(() -> client.createBatchRequest()
                        .add(1L, "findPlayer", "Steven", "Stamkos")
                        .returnType(Player.class)
                        .keysType(String.class)
                        .execute())
                .withMessage("Id: '1' has wrong type: 'Long'. Should be: 'String'");
    }

    @Test
    public void testIOError() {
        JsonRpcClient client = new JsonRpcClient(request -> {
            throw new IOException("Network is down");
        });

        assertThatIllegalStateException().isThrownBy(() ->
                        client.createBatchRequest()
                                .add(1L, "findPlayer", "Steven", "Stamkos")
                                .add(2L, "findPlayer", "Vladimir", "Sobotka")
                                .keysType(Long.class)
                                .returnType(Player.class)
                                .execute())
                .withMessage("I/O error during a request processing");
    }

    @Test
    public void testFailFastOnNotJsonData() {
        assertThatIllegalArgumentException().isThrownBy(() -> client.createBatchRequest()
                        .add(1L, "findPlayer", new Name("Steven"), new Name("Stamkos"))
                        .add(2L, "findPlayer", new Name("Vladimir"), new Name("Sobotka"))
                        .keysType(Long.class)
                        .returnType(Player.class)
                        .execute())
                .withMessageStartingWith("No serializer found");
    }

    private static class Name {
        private final String value;

        private Name(String value) {
            this.value = value;
        }
    }

    @Test
    public void testNotArrayResponse() {
        JsonRpcClient client = new JsonRpcClient(request -> """
                {"test":"data"}
                """);
        assertThatIllegalStateException().isThrownBy(() -> client.createBatchRequest()
                        .add(1L, "findPlayer", "Steven", "Stamkos")
                        .add(2L, "findPlayer", "Vladimir", "Sobotka")
                        .returnType(Player.class)
                        .execute())
                .withMessage("Expected array but was OBJECT");
    }

    @Test
    public void testNotJsonResponse() {
        JsonRpcClient client = new JsonRpcClient(request -> "test data");
        assertThatIllegalStateException().isThrownBy(() -> client.createBatchRequest()
                        .add(1L, "findPlayer", "Steven", "Stamkos")
                        .add(2L, "findPlayer", "Vladimir", "Sobotka")
                        .returnType(Player.class)
                        .execute())
                .withMessageStartingWith("Unable parse a JSON response");
    }

    @Test
    public void testNoVersion() {
        JsonRpcClient client = new JsonRpcClient(request -> """
                [{"test":"data"}]
                """);
        assertThatIllegalStateException().isThrownBy(() -> client.createBatchRequest()
                        .add(1L, "findPlayer", "Steven", "Stamkos")
                        .add(2L, "findPlayer", "Vladimir", "Sobotka")
                        .returnType(Player.class)
                        .execute())
                .withMessageStartingWith("Not a JSON-RPC response");
    }

    @Test
    public void testBadVersion() {
        JsonRpcClient client = new JsonRpcClient(request -> """
                [{
                    "jsonrpc": "1.0",
                    "id": 1,
                    "result": {
                        "firstName": "Steven",
                        "lastName": "Stamkos",
                        "team": {
                            "name": "Tampa Bay Lightning",
                            "league": "NHL"
                        },
                        "number": 91,
                        "position": "C",
                        "birthDate": "1990-02-07T00:00:00.000+00:00",
                        "capHit": 7.5
                    }
                }]
                """);
        assertThatIllegalStateException().isThrownBy(() -> client.createBatchRequest()
                        .add(1L, "findPlayer", "Steven", "Stamkos")
                        .add(2L, "findPlayer", "Vladimir", "Sobotka")
                        .returnType(Player.class)
                        .execute())
                .withMessageStartingWith("Bad protocol version");
    }

    @Test
    public void testUnexpectedResult() {
        JsonRpcClient client = new JsonRpcClient(request -> """
                [{
                    "jsonrpc": "2.0",
                    "id": 1
                }]
                """);
        assertThatIllegalStateException().isThrownBy(() -> client.createBatchRequest()
                        .add(1L, "findPlayer", "Steven", "Stamkos")
                        .add(2L, "findPlayer", "Vladimir", "Sobotka")
                        .returnType(Player.class)
                        .execute())
                .withMessageStartingWith("Neither result or error is set in response");
    }

    @Test
    public void testUnspecifiedId() {
        JsonRpcClient client = new JsonRpcClient(request -> """
                [{
                    "jsonrpc": "2.0",
                    "id": 10,
                    "result": {
                        "firstName": "Steven",
                        "lastName": "Stamkos",
                        "team": {
                            "name": "Tampa Bay Lightning",
                            "league": "NHL"
                        },
                        "number": 91,
                        "position": "C",
                        "birthDate": "1990-02-07T00:00:00.000+00:00",
                        "capHit": 7.5
                    }
                }]
                """);
        assertThatIllegalStateException().isThrownBy(() -> client.createBatchRequest()
                        .add(1L, "findPlayer", "Steven", "Stamkos")
                        .returnType(Player.class)
                        .execute())
                .withMessage("Unspecified id: '10' in response");
    }

    @Test
    public void testJsonRpcError() {
        JsonRpcClient client = new JsonRpcClient(request -> """
                [{
                    "jsonrpc": "2.0",
                    "id": 1,
                    "result": {
                        "firstName": "Steven",
                        "lastName": "Stamkos",
                        "team": {
                            "name": "Tampa Bay Lightning",
                            "league": "NHL"
                        },
                        "number": 91,
                        "position": "C",
                        "birthDate": "1990-02-07T00:00:00.000+00:00",
                        "capHit": 7.5
                    }
                },
                {"jsonrpc":"2.0","id":2, "error":{"code":-32603,"message":"Internal error"}}]
                """);

        JsonRpcBatchException e = catchThrowableOfType(() ->
                client.createBatchRequest()
                        .add(1L, "findPlayer", "Steven", "Stamkos")
                        .add(2L, "findPlayer", "Vladimir", "Sobotka")
                        .returnType(Player.class)
                        .keysType(Long.class)
                        .execute(), JsonRpcBatchException.class);

        Map<?, ErrorMessage> errors = e.getErrors();
        Map<?, ?> successes = e.getSuccesses();
        System.out.println(successes);
        System.out.println(errors);

        Object result = successes.get(1L);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Player.class);
        assertThat(((Player) result).firstName()).isEqualTo("Steven");
        assertThat(((Player) result).lastName()).isEqualTo("Stamkos");

        assertThat(errors).isNotEmpty();
        ErrorMessage errorMessage = errors.get(2L);
        assertThat(errorMessage.getCode()).isEqualTo(-32603);
        assertThat(errorMessage.getMessage()).isEqualTo("Internal error");
    }
}
