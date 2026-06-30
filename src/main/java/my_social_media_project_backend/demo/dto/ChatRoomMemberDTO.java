package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomMemberDTO {
    private String id;
    private String chatRoomId;
    private Long userId;
    private String username;
    private List<StoryDTO> stories;
    private LocalDateTime userUpdatedAt;

    public ChatRoomMemberDTO(String id, String chatRoomId, Long userId, String username, LocalDateTime userUpdatedAt) {
        this(id, chatRoomId, userId, username, new ArrayList<>(), userUpdatedAt);
    }

    public ChatRoomMemberDTO(String id, String chatRoomId, Long userId, String username, List<StoryDTO> stories, LocalDateTime userUpdatedAt) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.username = username;
        this.stories = stories;
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

    public List<StoryDTO> getStories() {
        return stories;
    }

    public void setStories(List<StoryDTO> stories) {
        this.stories = stories;
    }
}
