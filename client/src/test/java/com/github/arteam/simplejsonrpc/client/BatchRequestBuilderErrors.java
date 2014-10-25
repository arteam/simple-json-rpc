package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.arteam.simplejsonrpc.client.domain.Player;
import org.hamcrest.core.StringStartsWith;
import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Map;

/**
 * Date: 10/23/14
 * Time: 11:55 PM
 *
 * @author Artem Prigoda
 */
public class BatchRequestBuilderErrors {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    JsonRpcClient client = new JsonRpcClient(new Transport() {
        @NotNull
        @Override
        public String pass(@NotNull String request) throws IOException {
            System.out.println(request);
            return "[{\n" +
                    "    \"jsonrpc\": \"2.0\",\n" +
                    "    \"id\": 1,\n" +
                    "    \"result\": {\n" +
                    "        \"firstName\": \"Steven\",\n" +
                    "        \"lastName\": \"Stamkos\",\n" +
                    "        \"team\": {\n" +
                    "            \"name\": \"Tampa Bay Lightning\",\n" +
                    "            \"league\": \"NHL\"\n" +
                    "        },\n" +
                    "        \"number\": 91,\n" +
                    "        \"position\": \"C\",\n" +
                    "        \"birthDate\": \"1990-02-07T00:00:00.000+0000\",\n" +
                    "        \"capHit\": 7.5\n" +
                    "    }\n" +
                    "}]";
        }
    });

    @Test
    public void testRequestsAreEmpty() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Requests are not set");

        client.createBatchRequest().execute();
    }

    @Test
    public void testRequestWithoutReturnType() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Return type isn't specified for request with id='1'");

        client.createBatchRequest().add(1L, "findPlayer", "Steven", "Stamkos")
                .execute();
    }

    @Test
    public void testBothSingleAndGlobalResponseTypeAreSet() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Common and detailed configurations of return types shouldn't be mixed");

        client.createBatchRequest().add(1L, "findPlayer", new Object[]{"Steven", "Stamkos"}, Player.class)
                .valuesType(Player.class)
                .execute();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBadId() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Wrong id=true");

        BatchRequestBuilder<?, ?> batchRequest = client.createBatchRequest();
        batchRequest.getRequests()
                .add(batchRequest.request(BooleanNode.TRUE, "findPlayer",
                        new ObjectMapper().createArrayNode().add("Steven").add("Stamkos")));
        batchRequest.valuesType(Player.class).execute();
    }

    @Test
    public void tesKeyIdIsNotExpectedType() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Id: '1' has wrong type: 'Long'. Should be: 'String'");

        client.createBatchRequest().add(1L, "findPlayer", "Steven", "Stamkos")
                .valuesType(Player.class)
                .keysType(String.class)
                .execute();
    }

    @Test
    public void testIOError() {
        JsonRpcClient client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                throw new IOException("Network is down");
            }
        });

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("I/O error during a request processing");

        client.createBatchRequest()
                .add(1L, "findPlayer", "Steven", "Stamkos")
                .add(2L, "findPlayer", "Vladimir", "Sobotka")
                .keysType(Long.class)
                .valuesType(Player.class)
                .execute();
    }

    @Test
    public void testFailFastOnNotJsonData() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(new StringStartsWith("No serializer found"));

        client.createBatchRequest()
                .add(1L, "findPlayer", new Name("Steven"), new Name("Stamkos"))
                .add(2L, "findPlayer", new Name("Vladimir"), new Name("Sobotka"))
                .keysType(Long.class)
                .valuesType(Player.class)
                .execute();
    }

    private static class Name {
        private String value;

        private Name(String value) {
            this.value = value;
        }
    }
}
