Simple-json-rpc
===================

Library for simple integration JSON-RPC 2.0 protocol into a Java application.

## JSON-RPC 2.0 service example

Annotate you service class with *JsonRpcMethod* and *JsonRpcParam* annotations.

* *JsonRpcMethod* marks a method as eligble for calling from the web.
* *JsonRpcParam* is a mandatory annotation for the method parameter and should contain parameter name (this is forced requirement because Java compiler doesn't retain information about parameter names in a class file and therefore this information is not available in runtime).
* *Optional* is used for marking method parameter as an optional, so the caller is able ignore it when invokes the method. 

```java
public class TeamService {

    private List<Player> players = Lists.newArrayList();

    @JsonRpcMethod
    public boolean add(@JsonRpcParam("player") Player s) {
        return players.add(s);
    }

    @JsonRpcMethod("find_by_birth_year")
    public List<Player> findByBirthYear(@JsonRpcParam("birth_year") 
                                        final int birthYear) {
        return ImmutableList.copyOf(Iterables.filter(players, new Predicate<Player>() {
            @Override
            public boolean apply(Player player) {
                int year = new DateTime(player.getBirthDate()).getYear();
                return year == birthYear;
            }
        }));
    }

    @JsonRpcMethod
    public Player findByInitials(@JsonRpcParam("firstName") final String firstName,
                                 @JsonRpcParam("lastName") final String lastName) {
        return Iterables.tryFind(players, new Predicate<Player>() {
            @Override
            public boolean apply(Player player) {
                return player.getFirstName().equals(firstName) &&
                        player.getLastName().equals(lastName);
            }
        }).orNull();
    }

    @JsonRpcMethod
    public List<Player> find(@Optional @JsonRpcParam("position") final Position position,
                             @Optional @JsonRpcParam("number")  final int number,
                             @Optional @JsonRpcParam("team") final Team team,
                             @Optional @JsonRpcParam("firstName") final String firstName,
                             @Optional @JsonRpcParam("lastName")  final String lastName,
                             @Optional @JsonRpcParam("birthDate") final Date birthDate,
                             @Optional @JsonRpcParam("capHit") final double capHit) {
        return Lists.newArrayList(Iterables.filter(players, new Predicate<Player>() {
            @Override
            public boolean apply(Player player) {
                if (position != null && !player.getPosition().equals(position)) 
                    return false;
                if (number != 0 && player.getNumber() != number) 
                    return false;
                if (team != null && !player.getTeam().equals(team))
                    return false;
                if (firstName != null && !player.getFirstName().equals(firstName)) 
                    return false;
                if (lastName != null && !player.getLastName().equals(lastName)) 
                    return false;
                if (birthDate != null && !player.getBirthDate().equals(birthDate))
                    return false;
                if (capHit != 0 && player.getCapHit() != capHit) 
                    return false;
                return true;
            }
        }));
    }

    @JsonRpcMethod
    public List<Player> getPlayers() {
        return players;
    }
```

Invoke the service through *JsonRpcServer*

```java
TeamService teamService = new TeamService();
JsonRpcServer rpcServer = new JsonRpcServer();
String textRequest = "{\n" +
                    "    \"jsonrpc\": \"2.0\",\n" +
                    "    \"method\": \"findByInitials\",\n" +
                    "    \"params\": {\n" +
                    "        \"firstName\": \"Kevin\",\n" +
                    "        \"lastName\": \"Shattenkirk\"\n" +
                    "    },\n" +
                    "    \"id\": \"92739\"\n" +
                    "}";
String response = rpcServer.handle(request, teamService);
```

See the full service [code](https://github.com/arteam/simple-json-rpc/blob/master/src/test/java/com/github/arteam/json/rpc/simple/service/TeamService.java) more examples in [tests](https://github.com/arteam/simple-json-rpc/tree/master/src/test/java/com/github/arteam/json/rpc/simple).
