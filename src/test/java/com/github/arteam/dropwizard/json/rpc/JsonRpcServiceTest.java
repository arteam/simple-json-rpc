package com.github.arteam.dropwizard.json.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.yammer.dropwizard.testing.integration.TestServer;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Date: 7/28/14
 * Time: 10:29 PM
 *
 * @author Artem Prigoda
 */
public class JsonRpcServiceTest {

    private static final String TEST_CONFIG = JsonRpcServiceTest.class.getResource("/json-rpc.yml")
            .getPath();
    private TestServer<JsonRpcConfiguration, JsonRpcService> testServer;

    private DefaultHttpClient client = new DefaultHttpClient();

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() throws Exception {
        testServer = TestServer.create(JsonRpcServiceTest.class, new JsonRpcService(), TEST_CONFIG);
        testServer.start();
    }

    @After
    public void tearDown() throws Exception {
        client.getConnectionManager().shutdown();

        if (testServer.isRunning()) {
            testServer.stop();
        }
    }

    @Test
    public void test() throws Exception {
        HttpPost post = new HttpPost("http://127.0.0.1:8080/team");
        post.setHeader(new BasicHeader("content-type", "application/json"));
        post.setEntity(new StringEntity("{\"jsonrpc\": \"2.0\", \"method\": \"add\", \"params\": " +
                "{\"player\": \"David Backes\"}, \"id\": 1}"));
        HttpEntity entity = client.execute(post).getEntity();
        String actual = EntityUtils.toString(entity, Charsets.UTF_8);
        String expected = Resources.toString(Resources.getResource(getClass(), "/testAddPlayer.json"), Charsets.UTF_8);
        Assert.assertEquals(mapper.readTree(expected), mapper.readTree(actual));
        EntityUtils.consume(entity);
    }
}
