package com.github.arteam.simplejsonrpc.client.generator;

import com.github.arteam.simplejsonrpc.client.JsonRpcClient;
import com.github.arteam.simplejsonrpc.client.Transport;
import com.github.arteam.simplejsonrpc.client.object.TeamService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.BitSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AtomicLongIdGeneratorTest {

    @Test
    public void test() throws Exception {
        final Gson mapper = new Gson();
        final BitSet numbers = new BitSet(100);
        JsonRpcClient client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass(@NotNull String request) throws IOException {
                System.out.println(request);
                JsonObject jsonNode = mapper.fromJson(request, JsonObject.class);
                long id = jsonNode.get("id").getAsLong();
                System.out.println("id=" + id);
                synchronized (numbers) {
                    numbers.set((int) id, true);
                }
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("jsonrpc", "2.0");
                jsonObject.add("result", JsonNull.INSTANCE);
                jsonObject.addProperty("id", id);
                return mapper.toJson(jsonObject);
            }
        });
        final TeamService teamService = client.onDemand(TeamService.class, new AtomicLongIdGenerator());
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < 100; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    teamService.findByCapHit(5.5);
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        Assert.assertEquals(numbers.cardinality(), 100);
    }
}