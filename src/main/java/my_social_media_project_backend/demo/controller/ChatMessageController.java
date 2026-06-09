package my_social_media_project_backend.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.ChatMessageDTO;
import my_social_media_project_backend.demo.service.ChatMessageService;
import my_social_media_project_backend.demo.service.ChatRoomMemberService;

@RestController
@RequestMapping("/api/chatmessage")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    private final ChatRoomMemberService chatRoomMemberService;

    public ChatMessageController(ChatMessageService chatMessageService, ChatRoomMemberService chatRoomMemberService) {
        this.chatMessageService = chatMessageService;
        this.chatRoomMemberService = chatRoomMemberService;
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getChatMessages(
            @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "10") Integer length,
            @RequestParam String chatRoomId
    ) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = new HashMap<>();

        try {
            List<ChatMessageDTO> chatMessages = chatMessageService.getChatMessageDTOs(chatRoomId, start, length);
            chatRoomMemberService.markAsRead(chatRoomId, currentUser.getUserId());
            response.put("chatMessages", chatMessages);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.put("error", "Something went wrong");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
