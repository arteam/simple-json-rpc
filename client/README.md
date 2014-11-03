## JSON-RPC 2.0 client

Simple JSON-RPC client is a convenient way to access JSON-RPC 2.0 server.
It exposes fluent-style type-safe API to generate requests and handling responses.

The basic class in API is `JsonRpcClient`. It's a factory for creating request builders.
You pass to it an implementation of `Transport`, that's actually sends a request through the network
and converts a response to a text implementation. Other optional argument is Jackson `ObjectMapper`,
that could be used for customization of JSON serializing and data binding.

After that `JsonRpcClient` is ready to create builders with configured transport and serialization.
Builders themselves are focused on processing actual requests data.
Builders are immutable and type-safe. Actual request is built only in the execution phase.

### Basic JSON-RPC request
````java
JsonRpcClient client = new JsonRpcClient(new Transport() {

    CloseableHttpClient httpClient = HttpClients.createDefault();

    @NotNull
    @Override
    public String pass(@NotNull String request) throws IOException {
        // Used Apache HttpClient 4.3.1 as an example
        HttpPost post = new HttpPost("http://json-rpc-server/team");
        post.setEntity(new StringEntity(request, Charsets.UTF_8));
        post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
        try (CloseableHttpResponse httpResponse = httpClient.execute(post)) {
            return EntityUtils.toString(httpResponse.getEntity(), Charsets.UTF_8);
        }
    }
});

Player player = client.createRequest()
        .method("findByInitials")
        .id(43121)
        .param("firstName", "Steven")
        .param("lastName", "Stamkos")
        .returnAs(Player.class)
        .execute();
````
See more examples for using API [here] (https://github.com/arteam/simple-json-rpc/blob/master/client/src/test/java/com/github/arteam/simplejsonrpc/client/JsonRpcClientTest.java)

### Notification JSON-RPC request
````
client.createNotification()
      .method("update")
      .param("cacheName", "profiles")
      .execute();
````

More examples for using API [here] (https://github.com/arteam/simple-json-rpc/blob/master/client/src/test/java/com/github/arteam/simplejsonrpc/client/JsonRpcClientNotifications.java)

### Batch JSON-RPC request
````
Map<String, Player> result = client.createBatchRequest()
     .add("43121", "findByInitials", "Steven", "Stamkos")
     .add("43122", "findByInitials", "Jack", "Allen")
     .keysType(String.class)
     .returnType(Player.class)
     .execute();
````

More examples for using API [here] (https://github.com/arteam/simple-json-rpc/blob/master/client/src/test/java/com/github/arteam/simplejsonrpc/client/BatchRequestBuilderTest.java)

## Setup
Maven:
```xml
<dependency>
   <groupId>com.github.arteam</groupId>
   <artifactId>simple-json-rpc-client</artifactId>
   <version>0.3</version>
</dependency>
```
Artifacts are available in [jCenter](https://bintray.com/bintray/jcenter) repository.

## Requirements

JDK 1.6 and higher

## Dependencies

* [Jackson](https://github.com/FasterXML/jackson) 2.4.1
* [IntelliJ IDEA Annotations](http://mvnrepository.com/artifact/com.intellij/annotations/12.0) 12.0
