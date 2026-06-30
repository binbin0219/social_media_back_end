package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;

public class StoryDTO {

    private final Long id;
    private final UserDTO user;
    private final MediaDTO media;
    private final boolean isViewed;
    private final Long viewCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;

    public StoryDTO(
        Long id,
        UserDTO user,
        MediaDTO media,
        boolean isViewed,
        Long viewCount,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
    ) {
        this.id = id;
        this.user = user;
        this.media = media;
        this.isViewed = isViewed;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public UserDTO getUser() {
        return user;
    }

    public MediaDTO getMedia() {
        return media;
    }

    public boolean getIsViewed() {
        return isViewed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public Long getViewCount() {
        return viewCount;
    }
}