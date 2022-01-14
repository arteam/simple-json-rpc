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
            assertThat(request.getJsonrpc()).isEqualTo("2.0");
            assertThat(request.getMethod()).isEqualTo("update");
            assertThat(request.getParams()).isEqualTo(mapper.createObjectNode().put("cacheName", "profiles"));
            assertThat(request.getId().isMissingNode()).isFalse();
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
            assertThat(request.getJsonrpc()).isEqualTo("2.0");
            assertThat(request.getMethod()).isEqualTo("setExpirationTime");
            assertThat(request.getParams()).isEqualTo(mapper.createArrayNode().add("profiles").add(20));
            assertThat(request.getId().isMissingNode()).isFalse();
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
