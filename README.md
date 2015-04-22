Simple JSON-RPC [![Build Status](https://travis-ci.org/arteam/simple-json-rpc.png?branch=master)](https://travis-ci.org/arteam/simple-json-rpc) [![Coverage Status](https://coveralls.io/repos/arteam/simple-json-rpc/badge.png?branch=master)](https://coveralls.io/r/arteam/simple-json-rpc?branch=master)
===================

Library for a simple integration [JSON-RPC 2.0](http://www.jsonrpc.org/specification) protocol into a Java application.

The motivation of this library is to provide a simple, fast and reliable way to integrate JSON-RPC protocol into your application in the server and client sides. 

You just need to configure `JsonRpcClient` or `JsonRpcServer` and implement transport. 

That's it - the library takes care for the rest. 

No manual JSON transformation, reflection code, error handling, etc. You just need to declare a service interface with annotations. Even this is not a requriement - there is a fluent API in the client side, if you don't wantto declare an interface.

The library is a complete JSON-RPC protocol implementation, so it should cover all the types of JSON-RPC requests (legal or mailformed).

The library doesn't depend on any transport protocol (HTTP, TCP or UDP), an application server or a DI library. 

The dependencies are: 

* **Jackson**, which is great for JSON parsing and databinding and
* **Guava**, which is great for caching and Optional values (needed only for server)
* **SL4J**, which is a standard for logging (needed only for server)
* **IntelliJ Annotation**, which is great for type safety (it optional actually)

Submodules
-----------

* [Client] (https://github.com/arteam/simple-json-rpc/tree/master/client)

* [Server] (https://github.com/arteam/simple-json-rpc/tree/master/server)
