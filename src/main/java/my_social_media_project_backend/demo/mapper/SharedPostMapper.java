package my_social_media_project_backend.demo.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import my_social_media_project_backend.demo.dto.PostAttachmentDTO;
import my_social_media_project_backend.demo.dto.SharedPostDTO;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.PostStatistic;
import my_social_media_project_backend.demo.service.FriendshipService;
import my_social_media_project_backend.demo.service.R2StorageService;

@Component
public class SharedPostMapper {

    private final R2StorageService r2StorageService;
    private final FriendshipService friendshipService;

    public SharedPostMapper(
        R2StorageService r2StorageService,
        FriendshipService friendshipService
    ) {
        this.r2StorageService = r2StorageService;
        this.friendshipService = friendshipService;
    }

    public SharedPostDTO toDto(Post sharedPost, Long currentUserId) {
        if (sharedPost == null) {
            return null;
        }

        PostStatistic statistic = sharedPost.getPostStatistic();

        Long likeCount = statistic != null
            ? statistic.getLikeCount()
            : 0L;

        Long commentCount = statistic != null
            ? statistic.getCommentCount()
            : 0L;

        List<PostAttachmentDTO> attachments = sharedPost.getAttachments() != null
            ? sharedPost.getAttachments()
                .stream()
                .map(att -> PostAttachmentMapper.toDto(
                    sharedPost,
                    att,
                    r2StorageService
                ))
                .toList()
            : Collections.emptyList();

        Friendship friendship = friendshipService.findByUserAndFriendId(sharedPost.getUser().getId(), currentUserId);

        return new SharedPostDTO(
            sharedPost.getId(),
            UserMapper.toDto(
                sharedPost.getUser(),
                null,
                null,
                null,
                null,
                null
            ),
            sharedPost.getContent(),
            likeCount,
            commentCount,
            attachments,
            sharedPost.getPrivacySetting(),
            sharedPost.getCommentStatus(),
            sharedPost.getIsSensitiveContent() == 1,
            FriendshipMapper.toDto(friendship, currentUserId),
            sharedPost.getCreateAt()
        );
    }
}