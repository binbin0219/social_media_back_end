package my_social_media_project_backend.demo.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import my_social_media_project_backend.demo.dto.FriendDTO;
import my_social_media_project_backend.demo.dto.PostAttachmentDTO;
import my_social_media_project_backend.demo.dto.PostDTO;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.PostStatistic;
import my_social_media_project_backend.demo.service.R2StorageService;

@Component
public class PostMapper {

    public PostMapper(
    ) {
    }

    public static PostDTO toDto(
        Post post, 
        List<FriendDTO> visibilityList, 
        boolean canComment, 
        boolean isLiked, 
        Friendship friendship, 
        Long currentUserId,
        R2StorageService r2StorageService
    ) {

        PostStatistic statistic = post.getPostStatistic();

        Long likeCount = statistic != null ? statistic.getLikeCount() : 0L;
        Long commentCount = statistic != null ? statistic.getCommentCount() : 0L;

        List<PostAttachmentDTO> attachments =
                post.getAttachments()
                        .stream()
                        .map(att -> PostAttachmentMapper.toDto(post, att, r2StorageService))
                        .toList();

        return new PostDTO(
                post.getId(),
                UserMapper.toDto(post.getUser(), null, null, null, null, null),
                post.getContent(),
                likeCount,
                commentCount,
                isLiked,
                attachments,
                post.getPrivacySetting(),
                post.getCommentStatus(),
                post.getIsSensitiveContent() == 1,
                visibilityList,
                canComment,
                FriendshipMapper.toDto(friendship, currentUserId),
                post.getCreateAt()
        );
    }
}