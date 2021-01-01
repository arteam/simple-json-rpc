package com.github.arteam.simplejsonrpc.server;

import com.github.arteam.simplejsonrpc.server.simple.service.*;
import com.github.arteam.simplejsonrpc.server.simple.service.test.TestService1;
import com.github.arteam.simplejsonrpc.server.simple.service.test.TestService2;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class JsonRpcServerRegistryTest {


    @Test
    public void testBindServices() {


        assertThrows( IllegalArgumentException.class, () -> {
            final JsonRpcServer server = new JsonRpcServer();
            final JsonRpcServerRegistry registry = new JsonRpcServerRegistry( server, new BaseService());
        });

        assertDoesNotThrow( () -> {
            final JsonRpcServer server = new JsonRpcServer();
            final TestService1 deafultService = new TestService1();
            final JsonRpcServerRegistry registry = new JsonRpcServerRegistry( server, deafultService);

            {
                final Optional<Object> optService = registry.lookup("undefined service");

                assertTrue(optService.isPresent());
                assertEquals(deafultService, optService.get());
            }

            {
                final TestService2 service = new TestService2();
                registry.bind( "test2", service );

                final Optional<Object> optService = registry.lookup("test2");

                assertTrue(optService.isPresent());
                assertEquals(service, optService.get());

            }



        });

    }
}
