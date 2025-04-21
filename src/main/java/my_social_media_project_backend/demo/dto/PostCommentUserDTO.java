package my_social_media_project_backend.demo.dto;

public class PostCommentUserDTO {
    private Long id;
    private String avatar;
    private String username;

    public PostCommentUserDTO(Long id, String avatar, String username) {
        this.id = id;
        this.avatar = avatar;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }
}
