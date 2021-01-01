package com.github.arteam.simplejsonrpc.client.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arteam.simplejsonrpc.client.JsonRpcClient;
import com.github.arteam.simplejsonrpc.client.Transport;
import com.github.arteam.simplejsonrpc.client.object.TeamService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.BitSet;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AtomicLongIdGeneratorTest {

    @Test
    public void test() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final BitSet numbers = new BitSet(100);
        JsonRpcClient client = new JsonRpcClient(new Transport() {
            @NotNull
            @Override
            public String pass( @NotNull Optional<String> serviceName, @NotNull String request) throws IOException {
                System.out.println(request);
                JsonNode jsonNode = mapper.readTree(request);
                long id = jsonNode.get("id").asLong();
                System.out.println("id=" + id);
                synchronized (numbers) {
                    numbers.set((int) id, true);
                }
                return mapper.writeValueAsString(mapper.createObjectNode()
                        .put("jsonrpc", "2.0")
                        .putNull("result")
                        .put("id", id));
            }
        });

        final TeamService teamService =
                client.onDemand(TeamService.class)
                    .idGenerator(new AtomicLongIdGenerator())
                    .build();

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
        Assertions.assertEquals(numbers.cardinality(), 100);
    }
}