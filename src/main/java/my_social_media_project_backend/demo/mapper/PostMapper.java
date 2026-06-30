package my_social_media_project_backend.demo.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import my_social_media_project_backend.demo.dto.FriendDTO;
import my_social_media_project_backend.demo.dto.PostAttachmentDTO;
import my_social_media_project_backend.demo.dto.PostDTO;
import my_social_media_project_backend.demo.dto.UserDTO;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.Post;

@Component
public class PostMapper {

    public PostDTO toDto(
        Post post,
        List<FriendDTO> visibilityList,
        boolean canComment,
        boolean isLiked,
        Friendship friendship,
        Long likeCount,
        Long commentCount,
        Long shareCount,
        List<PostAttachmentDTO> attachments,
        PostDTO sharedPostDTO,
        UserDTO userDTO
    ) {

        return new PostDTO(
            post.getId(),

            userDTO,

            post.getContent(),

            likeCount != null ? likeCount : 0L,
            commentCount != null ? commentCount : 0L,
            shareCount != null ? shareCount : 0L,

            isLiked,

            attachments,

            post.getPrivacySetting(),
            post.getCommentStatus(),
            post.getIsSensitiveContent() == 1,

            visibilityList,

            canComment,

            FriendshipMapper.toDto(friendship, null),

            sharedPostDTO,

            post.getCreateAt()
        );
    }
}
