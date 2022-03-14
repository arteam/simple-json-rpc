package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * Date: 07.06.14
 * Time: 12:34
 * <p>Base representation of a JSON-RPC response (success or error)</p>
 */
public interface Response {

    String jsonrpc();

    ValueNode id();
}
