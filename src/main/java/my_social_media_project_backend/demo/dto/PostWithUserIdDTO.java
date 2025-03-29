package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;

public class PostWithUserIdDTO extends PostDTO{
    private final Integer userId;

    public PostWithUserIdDTO(Integer id, String title, String content, long likeCount, long commentCount, boolean isLiked, LocalDateTime create_at, Integer userId) {
        super(id, title, content, likeCount, commentCount, isLiked, create_at);
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}
