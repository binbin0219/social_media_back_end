package my_social_media_project_backend.demo.controller;

import jakarta.validation.Valid;
import my_social_media_project_backend.demo.UserSessionRegistry;
import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.ChatRoomDTO;
import my_social_media_project_backend.demo.dto.request.SendPrivateMessageRequest;
import my_social_media_project_backend.demo.dto.response.SendPrivateMessageResponse;
import my_social_media_project_backend.demo.service.ChatRoomMemberService;
import my_social_media_project_backend.demo.service.ChatRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final ChatRoomMemberService chatRoomMemberService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserSessionRegistry userSessionRegistry;

    public ChatController(ChatRoomService chatRoomService, ChatRoomMemberService chatRoomMemberService, SimpMessagingTemplate messagingTemplate, UserSessionRegistry userSessionRegistry) {
        this.chatRoomService = chatRoomService;
        this.chatRoomMemberService = chatRoomMemberService;
        this.messagingTemplate = messagingTemplate;
        this.userSessionRegistry = userSessionRegistry;
    }

    @MessageMapping("/chat.openChat")
    public void openChat(
            Principal principal
    ) {
        Long currentUserId = Long.valueOf(principal.getName());
        userSessionRegistry.setChatOpen(currentUserId, true);

        String activeChatRoomId = userSessionRegistry.getActiveChatRoomId(currentUserId);
        if(activeChatRoomId != null) {
            chatRoomMemberService.markAsRead(activeChatRoomId, currentUserId);
        }
    }

    @MessageMapping("/chat.closeChat")
    public void closeChat(
            Principal principal
    ) {
        userSessionRegistry.setChatOpen(Long.valueOf(principal.getName()), false);
    }

    @MessageMapping("/chat.setActiveChatRoomId")
    public void setActiveChatRoomId(
            String chatRoomId,
            Principal principal
    ) {
        Long currentUserId = Long.valueOf(principal.getName());

        if(Objects.equals(chatRoomId, "null")) {
            userSessionRegistry.setActiveChatRoomId(currentUserId, null);
        } else {
            userSessionRegistry.setActiveChatRoomId(currentUserId, chatRoomId);
            chatRoomMemberService.markAsRead(chatRoomId, currentUserId);
        }
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(
            SendPrivateMessageRequest req,
            Principal principal
    ) {

        SendPrivateMessageResponse response = chatRoomService.sendPrivateMessage(
                Long.valueOf(principal.getName()),
                req.peerId(),
                req.text()
        );

        messagingTemplate.convertAndSendToUser(
                String.valueOf(req.peerId()),
                "/queue/privateMessages",
                response
        );

        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/privateMessages",
                response
        );
    }

    @MessageMapping("/chat.initPrivateChat")
    public void initPrivateChat(
            SendPrivateMessageRequest req,
            Principal principal
    ) {
        Long currentUserId = Long.valueOf(principal.getName());

        try {
            ChatRoomDTO chatRoomDTO = chatRoomService.initPrivateChatRoom(currentUserId, req.peerId(), req.text());


            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/privateChatInit",
                    chatRoomDTO
            );

            chatRoomDTO.setUnreadCount(1L);
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(req.peerId()),
                    "/queue/privateChatInit",
                    chatRoomDTO
            );
        } catch (Exception e) {
            e.printStackTrace();
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/errors",
                    "Failed to initialize chat room."
            );
        }
    }
}
