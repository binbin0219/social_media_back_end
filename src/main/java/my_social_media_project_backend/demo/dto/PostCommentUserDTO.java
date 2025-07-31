package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;

public class PostCommentUserDTO {
    private Long id;
    private String username;
    private LocalDateTime updatedAt;

    public PostCommentUserDTO(Long id, String username, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
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
}
