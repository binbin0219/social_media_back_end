package my_social_media_project_backend.demo.dto;

import my_social_media_project_backend.demo.service.UserService;

import java.time.LocalDateTime;

public class PostCommentDTO {
    private final Integer id;
    private final String content;
    private final UserDTO user;
    private final LocalDateTime createAt;

    public PostCommentDTO(Integer id, String content, UserDTO user, LocalDateTime createAt) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.createAt = createAt;
    }

    public Integer getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public UserDTO getUser() {
        return user;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
}
