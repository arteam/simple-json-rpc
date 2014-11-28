package com.github.arteam.simplejsonrpc.client;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Date: 24.08.14
 * Time: 18:25
 *
 * @author Artem Prigoda
 */
public class BaseClientTest {

    private static Map<String, RequestResponse> requestsResponses;

    private static Gson gson = GsonProvider.get();

    @BeforeClass
    public static void load() throws Exception {
        requestsResponses = new Gson().fromJson(
                Resources.toString(BaseClientTest.class.getResource("/client_test_data.json"), Charsets.UTF_8),
                new TypeToken<Map<String, RequestResponse>>() {
                }.getType());
    }

    protected JsonRpcClient initClient(String testName) {
        final RequestResponse requestResponse = requestsResponses.get(testName);
        return new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                System.out.println(request);
                Gson mapper = new GsonBuilder().serializeNulls().create();
                JsonElement requestNode = mapper.fromJson(request, JsonElement.class);
                assertThat(requestNode).isEqualTo(requestResponse.request);
                String response = gson.toJson(requestResponse.response);
                System.out.println(response);
                return response;
            }
        }, gson);
    }

    protected JsonRpcClient fakeClient() {
        return new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                return "";
            }
        }, gson);
    }
}
