package com.github.arteam.dropwizard.json.rpc;

import com.github.arteam.dropwizard.json.rpc.web.TeamResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

/**
 * Date: 7/27/14
 * Time: 8:37 PM
 *
 * @author Artem Prigoda
 */
public class Main extends Service<JsonRpcConfiguration> {

    @Override
    public void initialize(Bootstrap<JsonRpcConfiguration> bootstrap) {
        bootstrap.setName("json-rpc");
    }

    @Override
    public void run(JsonRpcConfiguration configuration, Environment environment) throws Exception {
       environment.addResource(new TeamResource());
    }

    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }
}
