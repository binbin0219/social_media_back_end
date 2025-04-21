package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;

public class PostWithUserIdDTO extends PostDTO{
    private final Long userId;

    public PostWithUserIdDTO(Long id, String title, String content, long likeCount, long commentCount, boolean isLiked, LocalDateTime create_at, Long userId) {
        super(id, title, content, likeCount, commentCount, isLiked, create_at);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
