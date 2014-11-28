package com.github.arteam.simplejsonrpc.client;

import com.github.arteam.simplejsonrpc.client.domain.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;

/**
 * Date: 8/17/14
 * Time: 5:06 PM
 *
 * @author Artem Prigoda
 */
public class JsonRpcClientErrors {

    JsonRpcClient client = new JsonRpcClient(new Transport() {
        @NotNull
        @Override
        public String pass(@NotNull String request) throws IOException {
            System.out.println(request);
            return "{\"jsonrpc\": \"2.0\", \"id\": 1001, \"result\": true}";
        }
    });

    @Test(expected = IllegalArgumentException.class)
    public void testMethodIsNotSet() {
        client.createRequest()
                .execute();
    }


    @Test(expected = IllegalStateException.class)
    public void testBadJson() {
        client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                System.out.println(request);
                return "test";
            }
        });
        client.createRequest()
                .method("update")
                .id(1)
                .execute();
    }

    @Test(expected = IllegalStateException.class)
    public void testIOError() {
        client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                throw new IOException("Network is down");
            }
        });
        client.createRequest()
                .method("update")
                .id(1)
                .execute();
    }

    @Test(expected = IllegalStateException.class)
    public void testBadProtocolVersion() {
        client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                return "{\"jsonrpc\": \"1.0\", \"id\": 1001}";
            }
        });
        client.createRequest()
                .method("update")
                .id(1)
                .execute();
    }

    @Test(expected = IllegalStateException.class)
    public void notJsonRpc20Response() {
        client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                return "{\"some\":\"json\"}";
            }
        });
        client.createRequest()
                .method("update")
                .id(1)
                .execute();
    }

    @Test(expected = IllegalStateException.class)
    public void testIdIsNotSet() {
        client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                return "{\"jsonrpc\": \"2.0\"}";
            }
        });
        client.createRequest()
                .method("update")
                .id(1)
                .execute();
    }

    @Test(expected = IllegalStateException.class)
    public void testResultAndErrorAreNotSet() {
        JsonRpcClient client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                System.out.println(request);
                return "{\"jsonrpc\": \"2.0\", \"id\": 1001}";
            }
        });
        client.createRequest()
                .method("update")
                .id(1)
                .execute();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testWrongBuilder() {
        client.createRequest()
                .method("update")
                .id(1)
                .params(1, 2)
                .param("test", "param")
                .returnAs(String.class)
                .execute();
    }

    @Test(expected = IllegalStateException.class)
    public void testExpectedNotNull() {
        JsonRpcClient client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                System.out.println(request);
                return "{\"jsonrpc\": \"2.0\", \"result\" : null, \"id\": 1001}";
            }
        });
        client.createRequest()
                .method("getPlayer")
                .id(1001)
                .returnAs(Player.class)
                .execute();
    }
}
