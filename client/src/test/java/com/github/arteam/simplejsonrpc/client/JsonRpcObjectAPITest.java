package com.github.arteam.simplejsonrpc.client;

import com.github.arteam.simplejsonrpc.client.domain.Player;
import com.github.arteam.simplejsonrpc.client.domain.Position;
import com.github.arteam.simplejsonrpc.client.domain.Team;
import com.github.arteam.simplejsonrpc.client.object.FixedIntegerIdGenerator;
import com.github.arteam.simplejsonrpc.client.object.FixedStringIdGenerator;
import com.github.arteam.simplejsonrpc.client.object.TeamService;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Date: 24.08.14
 * Time: 18:06
 *
 * @author Artem Prigoda
 */
public class JsonRpcObjectAPITest extends BaseClientTest {

    @Test
    public void testAddPlayer() {
        JsonRpcClient client = initClient("add_player");
        TeamService teamService = client.onDemand(TeamService.class, new FixedStringIdGenerator("asd671"));
        boolean result = teamService.add(new Player("Kevin", "Shattenkirk", new Team("St. Louis Blues", "NHL"), 22, Position.DEFENDER,
                ISODateTimeFormat.date().withZone(DateTimeZone.UTC).parseDateTime("1989-01-29").toDate(),
                4.25));
        assertThat(result).isTrue();
    }

    @Test
    public void findPlayerByInitials() {
        JsonRpcClient client = initClient("find_player");
        Player player = client.onDemand(TeamService.class, new FixedIntegerIdGenerator(43121)).findByInitials("Steven", "Stamkos");
        assertThat(player).isNotNull();
        assertThat(player.getFirstName()).isEqualTo("Steven");
        assertThat(player.getLastName()).isEqualTo("Stamkos");
    }

    @Test
    public void testPlayerIsNotFound() {
        JsonRpcClient client = initClient("player_is_not_found");
        Player player = client.onDemand(TeamService.class, new FixedIntegerIdGenerator(4111)).findByInitials("Vladimir", "Sobotka");
        assertThat(player).isNull();
    }

    @Test
    public void testReturnList() {
        JsonRpcClient client = initClient("findByBirthYear");
        List<Player> players = client.onDemand(TeamService.class, new FixedIntegerIdGenerator(5621)).findByBirthYear(1990);
        assertThat(players).isNotNull();
        assertThat(players).hasSize(3);
        assertThat(players.get(0).getLastName()).isEqualTo("Allen");
        assertThat(players.get(1).getLastName()).isEqualTo("Stamkos");
        assertThat(players.get(2).getLastName()).isEqualTo("Hedman");
    }
}
