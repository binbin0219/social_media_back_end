package my_social_media_project_backend.demo.controller;

import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.service.FriendshipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/friendship")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @GetMapping("/request/send")
    public ResponseEntity<Map<String, String>> sendFriendRequest(
            @RequestParam("friendId") String friendId
    ) {
        Map<String, String> response = new HashMap<>();
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            friendshipService.sendFriendRequestByIds(userDetails.getUserId(), Integer.parseInt(friendId));
        } catch (Exception e) {
            response.put("error", "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.put("message", "Friend request sent successfully");
        return ResponseEntity.ok().body(response);
    }
}
