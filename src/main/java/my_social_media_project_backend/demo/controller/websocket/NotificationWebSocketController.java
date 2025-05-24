package my_social_media_project_backend.demo.controller.websocket;

import my_social_media_project_backend.demo.UserSessionRegistry;
import my_social_media_project_backend.demo.service.UserStatisticService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class NotificationWebSocketController {
    private final UserSessionRegistry userSessionRegistry;
    private final UserStatisticService userStatisticService;

    public NotificationWebSocketController(UserSessionRegistry userSessionRegistry, UserStatisticService userStatisticService) {
        this.userSessionRegistry = userSessionRegistry;
        this.userStatisticService = userStatisticService;
    }

    @MessageMapping("/notification.open")
    public void openNotification(
            Principal principal
    ) {
        Long currentUserId = Long.valueOf(principal.getName());
        userSessionRegistry.setNotificationOpen(currentUserId, true);
        userStatisticService.clearNewNotificationCount(currentUserId);
    }

    @MessageMapping("/notification.close")
    public void closeNotification(
            Principal principal
    ) {
        userSessionRegistry.setNotificationOpen(Long.valueOf(principal.getName()), false);
    }
}
