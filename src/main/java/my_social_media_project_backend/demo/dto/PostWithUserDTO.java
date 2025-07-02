package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostWithUserDTO extends PostDTO{

    private final UserDTO user;

    public PostWithUserDTO(Long id, String title, String content, long likeCount, long commentCount, boolean isLiked, LocalDateTime create_at, UserDTO user, List<PostAttachmentDTO> attachments) {
        super(id, title, content, likeCount, commentCount, isLiked, attachments, create_at);
        this.user = user;
    }

    public UserDTO getUser() {
        return user;
    }
}
