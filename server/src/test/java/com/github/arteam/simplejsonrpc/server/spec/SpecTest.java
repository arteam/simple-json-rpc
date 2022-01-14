package com.github.arteam.simplejsonrpc.server.spec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arteam.simplejsonrpc.server.JsonRpcServer;
import com.google.common.cache.CacheBuilderSpec;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Date: 7/31/14
 * Time: 8:51 PM
 */
public class SpecTest {

    final CalculatorService calculatorService = new CalculatorService();
    // Just test factory method
    final JsonRpcServer rpcServer = JsonRpcServer.withCacheSpec(CacheBuilderSpec.disableCaching());
    final ObjectMapper mapper = new ObjectMapper();

    private void test(String testName) {
        try (InputStream stream = SpecTest.class.getResourceAsStream("/spec/" + testName + ".properties")) {
            Properties testProps = new Properties();
            testProps.load(stream);
            String textRequest = testProps.getProperty("request");
            JsonNode response = mapper.readTree(testProps.getProperty("response"));

            String actual = rpcServer.handle(textRequest, calculatorService);
            if (!actual.isEmpty()) {
                assertThat(mapper.readTree(actual)).isEqualTo(response);
            } else {
                assertThat(actual).isEqualTo(response.asText());
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void test1() {
        test("test_1");
    }

    @Test
    public void test2() {
        test("test_2");
    }

    @Test
    public void test3() {
        test("test_3");
    }

    @Test
    public void test4() {
        test("test_4");
    }

    @Test
    public void test5() {
        test("test_5");
    }

    @Test
    public void test6() {
        test("test_6");
    }

    @Test
    public void test7() {
        test("test_7");
    }

    @Test
    public void test8() {
        test("test_8");
    }

    @Test
    public void test9() {
        test("test_9");
    }

    @Test
    public void test10() {
        test("test_10");
    }

    @Test
    public void test11() {
        test("test_11");
    }

    @Test
    public void test12() {
        test("test_12");
    }

    @Test
    public void test13() {
        test("test_13");
    }

    @Test
    public void test14() {
        test("test_14");
    }

    @Test
    public void test15() {
        test("test_15");
    }
}
