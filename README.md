Simple JSON-RPC 
[![Build Status](https://travis-ci.org/arteam/simple-json-rpc.png?branch=master)](https://travis-ci.org/arteam/simple-json-rpc) [![Coverage Status](https://coveralls.io/repos/github/arteam/simple-json-rpc/badge.svg?branch=master)](https://coveralls.io/github/arteam/simple-json-rpc?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.arteam/simple-json-rpc-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.arteam/simple-json-rpc-client/)
===================

A library for a simple integration of the [JSON-RPC 2.0](http://www.jsonrpc.org/specification) protocol to a Java application.

The motivation of this library is to provide a simple, fast and reliable way to integrate the JSON-RPC 2.0 protocol into your application at the server or client side.  For that you need to configure either `JsonRpcClient` or `JsonRpcServer` and implement transport code - the library takes care for the rest. No manual JSON transformation, reflection code, error handling - just a service interface with annotations. Even this is not a requriement - there is a fluent API at the client side if you like DSL builders. The library is a full-compilant JSON-RPC 2.0 protocol implementation, so it should handle all kind of JSON-RPC requests (correct or mailformed). It doesn't depend on any transport protocol, an application server, or a DI library. 

The library has a few dependencies: 

* **Jackson**, which is great for JSON parsing and databinding;
* **Guava**, which is great for caching and optional values (needed only for server)
* **SL4J**, which is the standard for logging (needed only for server)
* **IntelliJ Annotation**, which is great for providing compiler-time null checks.

Submodules
-----------

* [Client](https://github.com/arteam/simple-json-rpc/tree/master/client)

* [Server](https://github.com/arteam/simple-json-rpc/tree/master/server)
