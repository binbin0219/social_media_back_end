package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostCommentUserDTO {
    private Long id;
    private String username;
    private FriendshipDTO friendship;
    private List<StoryDTO> stories;
    private LocalDateTime updatedAt;

    public PostCommentUserDTO(Long id, String username, FriendshipDTO friendship, List<StoryDTO> stories, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.friendship = friendship;
        this.stories = stories;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public FriendshipDTO getFriendship() {
        return friendship;
    }

    public List<StoryDTO> getStories() {
        return stories;
    }

    public void setStories(List<StoryDTO> stories) {
        this.stories = stories;
    }

    public void setFriendship(FriendshipDTO friendship) {
        this.friendship = friendship;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
