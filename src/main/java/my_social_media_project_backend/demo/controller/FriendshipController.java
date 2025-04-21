package my_social_media_project_backend.demo.controller;

import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.exception.CannotAcceptFriendRequestException;
import my_social_media_project_backend.demo.exception.CannotSendFriendRequestException;
import my_social_media_project_backend.demo.exception.CannotUnsendFriendRequestException;
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
            friendshipService.sendFriendRequestByIds(userDetails.getUserId(), Long.parseLong(friendId));
        } catch (CannotSendFriendRequestException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("error", "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.put("message", "Friend request sent successfully");
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/request/unsend")
    public ResponseEntity<Map<String, String>> unsendFriendRequest(
            @RequestParam("friendId") String friendId
    ) {
        Map<String, String> response = new HashMap<>();
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            friendshipService.unsendFriendRequest(userDetails.getUserId(), Long.parseLong(friendId));
        } catch (CannotUnsendFriendRequestException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("error", "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.put("message", "Friend request unsent successfully");
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/request/accept")
    public ResponseEntity<Map<String, String>> acceptFriendRequest(
            @RequestParam("friendId") String friendId
    ) {
        Map<String, String> response = new HashMap<>();
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            friendshipService.acceptFriendRequestByIds(Long.parseLong(friendId), userDetails.getUserId());
        } catch (CannotAcceptFriendRequestException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.put("error", "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.put("message", "Friend request accepted successfully");
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/request/reject")
    public ResponseEntity<Map<String, String>> rejectFriendRequest(
            @RequestParam("friendId") String friendId
    ) {
        Map<String, String> response = new HashMap<>();
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            friendshipService.rejectFriendRequestByIds(Long.parseLong(friendId), userDetails.getUserId());
        } catch (CannotAcceptFriendRequestException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.put("error", "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.put("message", "Friend request accepted successfully");
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/unfriend")
    public ResponseEntity<Map<String, String>> unfriend(
            @RequestParam("friendId") String friendId
    ) {
        Map<String, String> response = new HashMap<>();
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            friendshipService.unfriend(userDetails.getUserId(), Long.parseLong(friendId));
        } catch (Exception e) {
            response.put("error", "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.put("message", "Unfriended successfully");
        return ResponseEntity.ok().body(response);
    }
}
