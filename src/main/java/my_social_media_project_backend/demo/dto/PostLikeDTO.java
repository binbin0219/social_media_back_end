package my_social_media_project_backend.demo.dto;

public class PostLikeDTO {
    private final Long user_id;

    public PostLikeDTO(Long user_id) {
        this.user_id = user_id;
    }

    public Long getUser_id() {
        return user_id;
    }
}
