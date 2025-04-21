package my_social_media_project_backend.demo.controller;

import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.NotificationDTO;
import my_social_media_project_backend.demo.entity.Notification;
import my_social_media_project_backend.demo.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteNotification(
            @RequestBody Map<String, Long> requestBody
    ) {
        Map<String, Object> response = new HashMap<>();
        Long notificationId = requestBody.get("notificationId");

        try{
            notificationService.deleteById(notificationId);
        } catch (Exception e) {
            response.put("error", "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.put("message", "Notification deleted successfully");
        return ResponseEntity.ok().body(response);
    }
}
