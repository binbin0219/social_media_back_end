package my_social_media_project_backend.demo.controller;

import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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


        return ResponseEntity.ok().body(response);
    }
}
