package my_social_media_project_backend.demo.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_members")
public class ChatRoomMember {
    @Id
    @Column(length = 26, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "unread_count", nullable = false)
    private Long unreadCount = 0L;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public LocalDateTime getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(LocalDateTime lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public Long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
