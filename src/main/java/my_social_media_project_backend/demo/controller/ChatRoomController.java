package my_social_media_project_backend.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.ChatRoomDTO;
import my_social_media_project_backend.demo.dto.request.SendPrivateMessageRequest;
import my_social_media_project_backend.demo.service.ChatRoomService;

@RestController
@RequestMapping("/api/chatroom")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getChatRooms(
            @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "10") Integer length
    ) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = new HashMap<>();

        try {
            List<ChatRoomDTO> chatRoomDTOS = chatRoomService.getChatRooms(currentUser.getUserId(), start, length);
            response.put("chatRooms", chatRoomDTOS);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("error", "Something went wrong");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/private/get")
    public ResponseEntity<Object> getPrivateRoom(
            @RequestParam("userId") Long userId
    ) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = new HashMap<>();

        try {
            ChatRoomDTO chatRoomDTO = chatRoomService.getPrivateRoom(userId, currentUser.getUserId());
            response.put("chatRoom", chatRoomDTO);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("error", "Something went wrong");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/private/init")
    public ResponseEntity<Object> initPrivateRoom(
            @Valid @RequestBody SendPrivateMessageRequest req
    ) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = new HashMap<>();

        try {
            ChatRoomDTO chatRoomDTO = chatRoomService.initPrivateChatRoom(currentUser.getUserId(), req.peerId(), req.text());
            response.put("chatRoom", chatRoomDTO);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("error", "Something went wrong");
            return ResponseEntity.internalServerError().body(response);
        }
    }

}
