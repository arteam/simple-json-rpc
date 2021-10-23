Simple JSON-RPC 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.arteam/simple-json-rpc-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.arteam/simple-json-rpc-client/)
===================

Library for a simple integration of the [JSON-RPC 2.0](http://www.jsonrpc.org/specification) protocol to a Java application.

The goal is to provide a simple, fast and reliable way to integrate JSON-RPC 2.0 into a Java application on the server and/or the client side. You need to configure either `JsonRpcClient` or `JsonRpcServer` and implement transport code: the library takes care of the rest. No manual JSON transformation, reflection and manual error handling: just a service interface with annotations. Even this is not a requriement: there is a fluent API on the client side if you prefer builder-style APIs. The library is a JSON-RPC 2.0 compilant implementation, so it should support handle all kind of JSON-RPC requests (correct or malformed). It doesn't depend on any transport protocol, an application server, or a DI framework. 

The library has a few dependencies: 

* **Jackson**, which is great for JSON parsing and databinding;
* **Guava**, which is great for caching and optional values (needed only for the server side)
* **SL4J**, which is the standard for logging (needed only for server)
* **IntelliJ Annotations**, which is great for providing compiler-time null checks.

Submodules
-----------

* [Client](https://github.com/arteam/simple-json-rpc/tree/master/client)
* [Server](https://github.com/arteam/simple-json-rpc/tree/master/server)
