package ru.otus.homework.hw32.front.ws.handler;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.AbstractHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class AnonymousHandshakeHandler extends AbstractHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request.getPrincipal() != null) {
            return super.determineUser(request, wsHandler, attributes);
        }
        return new AnonymousStompPrincipal(UUID.randomUUID().toString());
    }

    public static class AnonymousStompPrincipal implements Principal {
        private final String name;

        public AnonymousStompPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
