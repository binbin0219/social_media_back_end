package my_social_media_project_backend.demo.dto.response;

import my_social_media_project_backend.demo.dto.ChatMessageDTO;

import java.time.LocalDateTime;

public class SendPrivateMessageResponse {
    private String chatRoomId;
    private ChatMessageDTO message;
    private String messagePreview;
    private LocalDateTime lastMessageAt;

    public SendPrivateMessageResponse(String chatRoomId, ChatMessageDTO message, String messagePreview, LocalDateTime lastMessageAt) {
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.messagePreview = messagePreview;
        this.lastMessageAt = lastMessageAt;
    }

    public ChatMessageDTO getMessage() {
        return message;
    }

    public void setMessage(ChatMessageDTO message) {
        this.message = message;
    }

    public String getMessagePreview() {
        return messagePreview;
    }

    public void setMessagePreview(String messagePreview) {
        this.messagePreview = messagePreview;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}

