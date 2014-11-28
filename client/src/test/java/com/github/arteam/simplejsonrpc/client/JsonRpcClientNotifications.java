package com.github.arteam.simplejsonrpc.client;

import com.github.arteam.simplejsonrpc.core.domain.Request;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Date: 8/17/14
 * Time: 11:54 PM
 *
 * @author Artem Prigoda
 */
public class JsonRpcClientNotifications {

    @Test
    public void testNotificationObjectParams() {
        JsonRpcClient client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String text) throws IOException {
                System.out.println(text);
                Gson mapper = new Gson();
                Request request = mapper.fromJson(text, Request.class);
                assertThat(request.getJsonrpc()).isEqualTo("2.0");
                assertThat(request.getMethod()).isEqualTo("update");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("cacheName", "profiles");
                assertThat(request.getParams()).isEqualTo(jsonObject);
                assertThat(request.getId().isJsonNull());
                return "";
            }
        });

        client.createNotification()
                .method("update")
                .param("cacheName", "profiles")
                .execute();
    }

    @Test
    public void testNotificationArrayParams() {
        JsonRpcClient client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String text) throws IOException {
                System.out.println(text);
                Gson mapper = new Gson();
                Request request = mapper.fromJson(text, Request.class);
                assertThat(request.getJsonrpc()).isEqualTo("2.0");
                assertThat(request.getMethod()).isEqualTo("setExpirationTime");
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(new JsonPrimitive("profiles"));
                jsonArray.add(new JsonPrimitive(20));
                assertThat(request.getParams()).isEqualTo(jsonArray);
                assertThat(request.getId().isJsonNull());
                return "";
            }
        });

        client.createNotification()
                .method("setExpirationTime")
                .params("profiles", 20)
                .execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMethodIsNotSet() {
        JsonRpcClient client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String text) throws IOException {
                System.out.println(text);
                return "";
            }
        });
        client.createNotification().execute();
    }

}
