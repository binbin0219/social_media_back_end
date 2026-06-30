package my_social_media_project_backend.demo.mapper;

import org.springframework.stereotype.Component;

import my_social_media_project_backend.demo.dto.PostCommentDTO;
import my_social_media_project_backend.demo.dto.PostCommentUserDTO;
import my_social_media_project_backend.demo.entity.Comment;

@Component
public class PostCommentMapper {

    public PostCommentMapper() {
    }

    public static PostCommentDTO toDto(Comment postComment, PostCommentUserDTO user) {
        return new PostCommentDTO(
                postComment.getId(),
                postComment.getContent(),
                user,
                postComment.getCreateAt()
        );
    }
}
