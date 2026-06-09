package my_social_media_project_backend.demo.mapper;

import org.springframework.stereotype.Component;

import my_social_media_project_backend.demo.dto.PostCommentDTO;
import my_social_media_project_backend.demo.entity.Comment;

@Component
public class PostCommentMapper {

    public PostCommentMapper() {
    }

    public static PostCommentDTO toDto(Comment postComment) {
        return new PostCommentDTO(
                postComment.getId(),
                postComment.getContent(),
                PostCommentUserMapper.toDto(postComment.getUser()),
                postComment.getCreateAt()
        );
    }
}