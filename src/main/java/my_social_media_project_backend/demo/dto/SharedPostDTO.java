package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

import my_social_media_project_backend.demo.enums.CommentStatus;
import my_social_media_project_backend.demo.enums.PostPrivacySetting;

public class SharedPostDTO {
    private final Long id;
    private final UserDTO user;
    private final String content;
    private final Long likeCount;
    private final Long commentCount;
    private final List<PostAttachmentDTO> attachments;
    private final PostPrivacySetting privacySetting;
    private final CommentStatus commentStatus;
    private final boolean isSensitive;
    private final FriendshipDTO friendship;
    private final LocalDateTime createdAt;

    public SharedPostDTO(
        Long id,
        UserDTO user,
        String content,
        Long likeCount,
        Long commentCount,
        List<PostAttachmentDTO> attachments,
        PostPrivacySetting privacySetting,
        CommentStatus commentStatus,
        boolean isSensitive,
        FriendshipDTO friendship,
        LocalDateTime createdAt
    ) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.attachments = attachments;
        this.privacySetting = privacySetting;
        this.commentStatus = commentStatus;
        this.isSensitive = isSensitive;
        this.friendship = friendship;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public UserDTO getUser() { return user; }
    public String getContent() { return content; }
    public Long getLikeCount() { return likeCount; }
    public Long getCommentCount() { return commentCount; }
    public List<PostAttachmentDTO> getAttachments() { return attachments; }
    public PostPrivacySetting getPrivacySetting() { return privacySetting; }
    public CommentStatus getCommentStatus() { return commentStatus; }
    public boolean getIsSensitive() { return isSensitive; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public FriendshipDTO getFriendship() {
        return friendship;
    }
}