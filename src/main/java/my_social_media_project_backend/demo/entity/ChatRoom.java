package my_social_media_project_backend.demo.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {

    public enum Type {
        PRIVATE, GROUP
    }

    @Id
    @Column(length = 26, updatable = false, nullable = false)
    private String id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(name = "message_preview")
    private String messagePreview;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ChatRoomMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ChatMessage> messages = new ArrayList<>();

    @Column(name = "create_at")
    private final LocalDateTime createAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UlidCreator.getUlid().toString();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<ChatRoomMember> getMembers() {
        return members;
    }

    public void setMembers(List<ChatRoomMember> members) {
        this.members = members;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
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
}
