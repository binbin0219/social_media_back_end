package my_social_media_project_backend.demo.dto;

import my_social_media_project_backend.demo.service.UserService;

import java.time.LocalDateTime;

public class PostCommentDTO {
    private final Long id;
    private final String content;
    private final PostCommentUserDTO user;
    private final LocalDateTime createAt;

    public PostCommentDTO(Long id, String content, PostCommentUserDTO user, LocalDateTime createAt) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.createAt = createAt;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public PostCommentUserDTO getUser() {
        return user;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
}
