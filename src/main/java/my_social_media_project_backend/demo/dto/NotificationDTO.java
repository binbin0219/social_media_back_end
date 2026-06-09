package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;

import my_social_media_project_backend.demo.enums.NotificationType;

public class NotificationDTO {
    private Long id;
    private Long recipientId;
    private Long senderId;
    private String senderUsername;
    private LocalDateTime senderUpdatedAt;
    private NotificationType type;
    private String content;
    private String link;
    private boolean seen;
    private Long targetId;
    private LocalDateTime createdAt;

    public NotificationDTO(Long id, Long recipientId, Long senderId, String senderUsername, LocalDateTime senderUpdatedAt, NotificationType type, String content, String link, boolean seen, Long targetId, LocalDateTime createdAt) {
        this.id = id;
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.senderUpdatedAt = senderUpdatedAt;
        this.type = type;
        this.content = content;
        this.link = link;
        this.seen = seen;
        this.targetId = targetId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public LocalDateTime getCreateAt() {
        return createdAt;
    }

    public void setCreateAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSenderUpdatedAt() {
        return senderUpdatedAt;
    }

    public void setSenderUpdatedAt(LocalDateTime senderUpdatedAt) {
        this.senderUpdatedAt = senderUpdatedAt;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
}
