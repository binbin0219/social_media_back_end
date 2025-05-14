package my_social_media_project_backend.demo.dto;

import my_social_media_project_backend.demo.entity.ChatAttachment;

import java.time.LocalDateTime;
import java.util.List;

public class ChatMessageDTO {
    private String id;
    private Long senderId;
    private String senderUsername;
    private String text;
    private List<ChatAttachment> attachments;
    private LocalDateTime createAt;

    public ChatMessageDTO(String id, Long senderId, String senderUsername, String text, List<ChatAttachment> attachments, LocalDateTime createAt) {
        this.id = id;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.text = text;
        this.attachments = attachments;
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public List<ChatAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ChatAttachment> attachments) {
        this.attachments = attachments;
    }
}
