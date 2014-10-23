package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.arteam.simplejsonrpc.client.domain.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Date: 10/23/14
 * Time: 11:55 PM
 *
 * @author Artem Prigoda
 */
public class BatchRequestBuilderErrors {

    JsonRpcClient client = new JsonRpcClient(new Transport() {
        @NotNull
        @Override
        public String pass(@NotNull String request) throws IOException {
            System.out.println(request);
            return "";
        }
    });

    @Test(expected = IllegalArgumentException.class)
    public void testRequestsAreEmpty() {
        client.createBatchRequest()
                .execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequestWithoutReturnType() {
        client.createBatchRequest().add(1L, "findPlayer", "Steven", "Stamkos")
                .execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBothSingleAndGlobalResponseTypeAreSet() {
        client.createBatchRequest().add(1L, "findPlayer", new Object[]{"Steven", "Stamkos"}, Player.class)
                .valuesType(Player.class)
                .execute();
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testBadId() throws Exception {
        BatchRequestBuilder<?, ?> batchRequest = client.createBatchRequest();
        batchRequest.getRequests()
                .add(batchRequest.request(BooleanNode.TRUE, "findPlayer", new ObjectMapper().createArrayNode()
                        .add("Steven")
                        .add("Stamkos")));
        batchRequest.valuesType(Player.class).execute();
    }
}
