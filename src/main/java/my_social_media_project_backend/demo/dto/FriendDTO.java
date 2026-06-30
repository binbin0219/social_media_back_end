package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FriendDTO {
    private long id;
    private String username;
    private List<StoryDTO> stories;
    private LocalDateTime updatedAt;

    public FriendDTO(long id, String username, List<StoryDTO> stories, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.stories = stories;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public List<StoryDTO> getStories() {
        return stories;
    }

    public void setStories(List<StoryDTO> stories) {
        this.stories = stories;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
