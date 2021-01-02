package com.github.arteam.simplejsonrpc.client.builder;

import com.github.arteam.simplejsonrpc.client.ParamsType;
import com.github.arteam.simplejsonrpc.client.metadata.ServiceMetadata;
import com.github.arteam.simplejsonrpc.client.metadata.MethodMetadata;
import com.github.arteam.simplejsonrpc.client.metadata.ServiceMetadataFactory;
import com.github.arteam.simplejsonrpc.client.object.TeamService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataTest {

    @Test
    public void testLoadTeamService( ) {

        final ServiceMetadataFactory mdFactory = new AnnotationsServiceMetadataFactory();

        final ServiceMetadata metadata = mdFactory.createServiceMetadata( TeamService.class );

        assertNotNull( metadata );

        assertNotNull( metadata.getParamsType() );
        assertEquals(ParamsType.MAP.name(), metadata.getParamsType().name() );

        final java.util.Map<Method, MethodMetadata> methods = metadata.getMethods();

        assertNotNull( methods );
        assertEquals( 14, methods.size() );

        final String methodNameArray[] = {
                "add",
                "find",
                "findByCapHit",
                "findByInitials",
                "findByInitials",
                "findPlayersByFirstNames",
                "findPlayersByNumbers",
                "find_by_birth_year",
                "genericFindPlayersByNumbers",
                "getContractSums",
                "getPlayer",
                "getPlayers",
                "login",
                "logout",
        };

        assertArrayEquals( methodNameArray,
                methods.values().stream().map( m -> m.getName() ).sorted().toArray());


    }
}
