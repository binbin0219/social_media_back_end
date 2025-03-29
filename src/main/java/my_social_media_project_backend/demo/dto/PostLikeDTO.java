package my_social_media_project_backend.demo.dto;

public class PostLikeDTO {
    private final Integer user_id;

    public PostLikeDTO(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getUser_id() {
        return user_id;
    }
}
