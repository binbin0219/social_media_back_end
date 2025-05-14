package my_social_media_project_backend.demo.custom;

import my_social_media_project_backend.demo.utility.CookieUtils;
import my_social_media_project_backend.demo.utility.JwtUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    private final CookieUtils cookieUtils;
    private final JwtUtils jwtUtils;

    public CustomHandshakeHandler(CookieUtils cookieUtils, JwtUtils jwtUtils) {
        this.cookieUtils = cookieUtils;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = cookieUtils.extractTokenFromCookie(request);
        Long userId = jwtUtils.getUserIdFromToken(token);

        return new Principal() {
            @Override
            public String getName() {
                return String.valueOf(userId); // Use userId as unique identifier
            }
        };
    }
}

