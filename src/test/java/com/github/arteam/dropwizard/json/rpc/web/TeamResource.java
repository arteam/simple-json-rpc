package com.github.arteam.dropwizard.json.rpc.web;

import com.github.arteam.dropwizard.json.rpc.protocol.controller.JsonRpcController;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.Response;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.Request;
import com.github.arteam.dropwizard.json.rpc.service.TeamService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Date: 7/27/14
 * Time: 9:57 PM
 *
 * @author Artem Prigoda
 */
@Path("team")
@Produces("application/json")
@Consumes("application/json")
public class TeamResource {

    private TeamService teamService = new TeamService();

    private JsonRpcController jsonRpcController = new JsonRpcController();

    @POST
    public String handle(String request) {
        return jsonRpcController.handle(request, teamService);
    }
}
