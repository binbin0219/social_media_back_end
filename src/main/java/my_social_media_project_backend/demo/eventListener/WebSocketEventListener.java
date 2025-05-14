package my_social_media_project_backend.demo.eventListener;

import my_social_media_project_backend.demo.UserSessionRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {
    private final UserSessionRegistry userSessionRegistry;

    public WebSocketEventListener(UserSessionRegistry userSessionRegistry) {
        this.userSessionRegistry = userSessionRegistry;
    }

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            userSessionRegistry.onConnect(Long.valueOf(user.getName()));
            System.out.println("User connected ! " + user.getName());
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            userSessionRegistry.onDisconnect(Long.valueOf(user.getName()));
        }
    }
}

