package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arteam.simplejsonrpc.core.domain.Request;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Date: 8/17/14
 * Time: 11:54 PM
 */
public class JsonRpcClientNotificationsTest {

    @Test
    public void testNotificationObjectParams() {
        JsonRpcClient client = new JsonRpcClient(text -> {
            System.out.println(text);
            ObjectMapper mapper = new ObjectMapper();
            Request request = mapper.readValue(text, Request.class);
            assertThat(request.jsonrpc()).isEqualTo("2.0");
            assertThat(request.method()).isEqualTo("update");
            assertThat(request.params()).isEqualTo(mapper.createObjectNode().put("cacheName", "profiles"));
            assertThat(request.id().isMissingNode()).isFalse();
            return "";
        });

        client.createNotification()
                .method("update")
                .param("cacheName", "profiles")
                .execute();
    }

    @Test
    public void testNotificationArrayParams() {
        JsonRpcClient client = new JsonRpcClient(text -> {
            System.out.println(text);
            ObjectMapper mapper = new ObjectMapper();
            Request request = mapper.readValue(text, Request.class);
            assertThat(request.jsonrpc()).isEqualTo("2.0");
            assertThat(request.method()).isEqualTo("setExpirationTime");
            assertThat(request.params()).isEqualTo(mapper.createArrayNode().add("profiles").add(20));
            assertThat(request.id().isMissingNode()).isFalse();
            return "";
        });

        client.createNotification()
                .method("setExpirationTime")
                .params("profiles", 20)
                .execute();
    }

    @Test
    public void testMethodIsNotSet() {
        JsonRpcClient client = new JsonRpcClient(text -> {
            System.out.println(text);
            return "";
        });
        assertThatIllegalArgumentException().isThrownBy(() -> client.createNotification().execute());
    }
}
