package my_social_media_project_backend.demo.controller;

import my_social_media_project_backend.demo.service.AvatarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping("random")
    public ResponseEntity<Object> getRandomAvatar(
        @RequestParam("gender") String gender
    ) {
        if (gender == null || gender.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Gender must be provided");
        }
        if(!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("female")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Gender must be either male or female");
        }

        try {
            String randomAvatarBase64 = avatarService.getRandomAvatarBase64(gender);
            return ResponseEntity.ok(Map.of("image", randomAvatarBase64));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get random avatar");
        }
    }
}
