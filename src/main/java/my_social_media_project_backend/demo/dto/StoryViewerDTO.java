package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class StoryViewerDTO {

    private final Long userId;
    private final String username;
    private final LocalDateTime updatedAt;
    private final List<StoryDTO> stories;
    private final FriendshipDTO friendship;

    public StoryViewerDTO(
        Long userId,
        String username,
        LocalDateTime updatedAt,
        List<StoryDTO> stories,
        FriendshipDTO friendship
    ) {
        this.userId = userId;
        this.username = username;
        this.updatedAt = updatedAt;
        this.stories = stories;
        this.friendship = friendship;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<StoryDTO> getStories() {
        return stories;
    }

    public FriendshipDTO getFriendship() {
        return friendship;
    }
}
