package my_social_media_project_backend.demo.dto;

public class ChatRoomMemberDTO {
    private String id;
    private String chatRoomId;
    private Long userId;
    private String username;
    private String avatar;

    public ChatRoomMemberDTO(String id, String chatRoomId, Long userId, String username, String avatar) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
