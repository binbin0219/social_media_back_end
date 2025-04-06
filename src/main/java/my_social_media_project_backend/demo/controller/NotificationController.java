package my_social_media_project_backend.demo.controller;

import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.NotificationDTO;
import my_social_media_project_backend.demo.entity.Notification;
import my_social_media_project_backend.demo.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> getNotifications(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "6") Integer recordPerPage
    ) {
        Map<String, Object> response = new HashMap<>();
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userDetails.getUserId(), offset, recordPerPage);
        List<NotificationDTO> unseenNotifications = notifications.stream().filter(notification -> !notification.isSeen()).toList();
        List<NotificationDTO> seenNotifications = notifications.stream().filter(NotificationDTO::isSeen).toList();
        response.put("unseenNotifications", unseenNotifications);
        response.put("seenNotifications", seenNotifications);
        return ResponseEntity.ok().body(response);
    }
}
