package com.github.arteam.simplejsonrpc.client;

import com.github.arteam.simplejsonrpc.client.domain.Player;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Date: 10/12/14
 * Time: 9:38 PM
 *
 * @author Artem Prigoda
 */
public class BatchRequestBuilderTest {

    @Test
    public void test() {
        Map<?, ?> result = new BatchRequestBuilder(null, null)
                .add(28L, "addPlayer", "Alex", "Pietrangelo")
                .add(29L, "addPlayer", "Kevin", "Shattenkirk")
                .add(30L, "addPlayer", new HashMap<String, Object>() {{
                    put("name", "Vladimir");
                    put("surname", "Tarasenko");
                }}, Player.class)
                .returnType(28L, Player.class)
                .execute();


    }
}
