package my_social_media_project_backend.demo.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @Column(length = 26, updatable = false, nullable = false)
    private String id;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UlidCreator.getUlid().toString();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room", nullable = false)
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "message", orphanRemoval = true)
    private List<ChatAttachment> attachments = new ArrayList<>();

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "create_at")
    private final LocalDateTime createAt = LocalDateTime.now();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
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

    public List<ChatAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ChatAttachment> attachments) {
        this.attachments = attachments;
    }
}
