package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private long id;
    private Integer recipientId;
    private Integer senderId;
    private String senderAvatar;
    private String senderFirstName;
    private String senderLastName;
    private String type;
    private String content;
    private String link;
    private boolean seen;
    private LocalDateTime createAt;

    public NotificationDTO(long id, Integer recipientId, Integer senderId, String senderAvatar, String senderFirstName, String senderLastName, String type, String content, String link, boolean seen, LocalDateTime createAt) {
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
        this.createAt = createAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
}
