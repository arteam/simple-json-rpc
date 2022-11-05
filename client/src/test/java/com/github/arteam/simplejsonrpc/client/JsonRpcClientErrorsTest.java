package com.github.arteam.simplejsonrpc.client;

import com.github.arteam.simplejsonrpc.client.domain.Player;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Date: 8/17/14
 * Time: 5:06 PM
 */
public class JsonRpcClientErrorsTest {

    private JsonRpcClient client = new JsonRpcClient(request -> {
        System.out.println(request);
        return "{\"jsonrpc\": \"2.0\", \"id\": 1001, \"result\": true}";
    });

    @Test
    public void testMethodIsNotSet() {
        assertThatIllegalArgumentException().isThrownBy(() -> client.createRequest().execute());
    }

    @Test
    public void testBadJson() {
        client = new JsonRpcClient(request -> {
            System.out.println(request);
            return "test";
        });
        assertThatIllegalStateException().isThrownBy(() -> client.createRequest()
                .method("update")
                .id(1)
                .execute());
    }

    @Test
    public void testIOError() {
        client = new JsonRpcClient(request -> {
            throw new IOException("Network is down");
        });
        assertThatIllegalStateException().isThrownBy(() -> client.createRequest()
                .method("update")
                .id(1)
                .execute());
    }

    @Test
    public void testBadProtocolVersion() {
        client = new JsonRpcClient(request -> "{\"jsonrpc\": \"1.0\", \"id\": 1001}");
        assertThatIllegalStateException().isThrownBy(() -> client.createRequest()
                .method("update")
                .id(1)
                .execute());
    }

    @Test
    public void notJsonRpc20Response() {
        client = new JsonRpcClient(request -> "{\"some\":\"json\"}");
        assertThatIllegalStateException().isThrownBy(() -> client.createRequest()
                .method("update")
                .id(1)
                .execute());
    }

    @Test
    public void testIdIsNotSet() {
        client = new JsonRpcClient(request -> "{\"jsonrpc\": \"2.0\"}");
        assertThatIllegalStateException().isThrownBy(() -> client.createRequest()
                .method("update")
                .id(1)
                .execute());
    }

    @Test
    public void testResultAndErrorAreNotSet() {
        JsonRpcClient client = new JsonRpcClient(request -> {
            System.out.println(request);
            return "{\"jsonrpc\": \"2.0\", \"id\": 1001}";
        });
        assertThatIllegalStateException().isThrownBy(() -> client.createRequest()
                .method("update")
                .id(1)
                .execute());
    }

    @Test
    public void testWrongBuilder() {
        assertThatIllegalArgumentException().isThrownBy(() -> client.createRequest()
                .method("update")
                .id(1)
                .params(1, 2)
                .param("test", "param")
                .returnAs(String.class)
                .execute());
    }

    @Test
    public void testExpectedNotNull() {
        JsonRpcClient client = new JsonRpcClient(request -> {
            System.out.println(request);
            return "{\"jsonrpc\": \"2.0\", \"result\" : null, \"id\": 1001}";
        });
        assertThatIllegalStateException().isThrownBy(() -> client.createRequest()
                .method("getPlayer")
                .id(1001)
                .returnAs(Player.class)
                .execute());
    }
}
