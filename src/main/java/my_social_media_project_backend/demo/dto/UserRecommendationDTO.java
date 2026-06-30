package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UserRecommendationDTO {
    private final Long id;
    private final String username;
    private final FriendshipDTO friendship;
    private final List<StoryDTO> stories;
    private final LocalDateTime updatedAt;

    public UserRecommendationDTO(
        Long id,
        String username,
        FriendshipDTO friendship,
        List<StoryDTO> stories,
        LocalDateTime updatedAt
    ) {
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

    public FriendshipDTO getFriendship() {
        return friendship;
    }

    public List<StoryDTO> getStories() {
        return stories;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
