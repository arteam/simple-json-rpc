package com.github.arteam.simplejsonrpc.client;

import java.io.IOException;

/**
 * Date: 8/9/14
 * Time: 8:52 PM
 * <p>Abstract transport for JSON-RPC communication</p>
 */
public interface Transport {

    /**
     * Passes a JSON-RPC request in a text form to a backend and
     * returns a JSON-RPC response in a text form as well
     *
     * @param request JSON-RPC request as a string
     * @return JSON-RPC response as a string
     * @throws IOException if an I/O error happens during transfer
     */
    public String pass(String request) throws IOException;
}
