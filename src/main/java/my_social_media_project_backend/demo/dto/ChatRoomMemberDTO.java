package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;

public class ChatRoomMemberDTO {
    private String id;
    private String chatRoomId;
    private Long userId;
    private String username;
    private LocalDateTime userUpdatedAt;

    public ChatRoomMemberDTO(String id, String chatRoomId, Long userId, String username, LocalDateTime userUpdatedAt) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.username = username;
        this.userUpdatedAt = userUpdatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getUserUpdatedAt() {
        return userUpdatedAt;
    }

    public void setUserUpdatedAt(LocalDateTime userUpdatedAt) {
        this.userUpdatedAt = userUpdatedAt;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
