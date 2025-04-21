package my_social_media_project_backend.demo.dto;

import my_social_media_project_backend.demo.entity.Notification;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private Long recipientId;
    private Long senderId;
    private String senderAvatar;
    private String senderFirstName;
    private String senderLastName;
    private Notification.Type type;
    private String content;
    private String link;
    private boolean seen;
    private Long targetId;
    private LocalDateTime createAt;

    public NotificationDTO(Long id, Long recipientId, Long senderId, String senderAvatar, String senderFirstName, String senderLastName, Notification.Type type, String content, String link, boolean seen, Long targetId, LocalDateTime createAt) {
        this.id = id;
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.senderAvatar = senderAvatar;
        this.senderFirstName = senderFirstName;
        this.senderLastName = senderLastName;
        this.type = type;
        this.content = content;
        this.link = link;
        this.seen = seen;
        this.targetId = targetId;
        this.createAt = createAt;
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

    public Notification.Type getType() {
        return type;
    }

    public void setType(Notification.Type type) {
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
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getSenderFirstName() {
        return senderFirstName;
    }

    public void setSenderFirstName(String senderFirstName) {
        this.senderFirstName = senderFirstName;
    }

    public String getSenderLastName() {
        return senderLastName;
    }

    public void setSenderLastName(String senderLastName) {
        this.senderLastName = senderLastName;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
}
