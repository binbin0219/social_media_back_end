package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import my_social_media_project_backend.demo.enums.CommentStatus;
import my_social_media_project_backend.demo.enums.PostPrivacySetting;

public class PostDTO {
    private final Long id;
    private final UserDTO user;
    private final String content;
    private final List<PostCommentDTO> comments = new ArrayList<>();
    private final Long likeCount;
    private final Long commentCount;
    private final boolean isLiked;
    private List<PostAttachmentDTO> attachments;
    private final PostPrivacySetting privacySetting;
    private final CommentStatus commentStatus;
    private final boolean isSensitive;
    private List<FriendDTO> visibilityFriendList = new ArrayList<>();
    private final boolean canComment;
    private final FriendshipDTO friendship;
    private final LocalDateTime created_at;

    public PostDTO(
        Long id,
        UserDTO user,
        String content,
        Long likeCount,
        Long commentCount,
        boolean isLiked,
        List<PostAttachmentDTO> attachments,
        PostPrivacySetting privacySetting,
        CommentStatus commentStatus,
        boolean isSensitive,
        List<FriendDTO> visibilityFriendList,
        boolean canComment,
        FriendshipDTO friendship,
        LocalDateTime created_at
    ) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLiked = isLiked;
        this.attachments = attachments;
        this.privacySetting = privacySetting;
        this.commentStatus = commentStatus;
        this.isSensitive = isSensitive;
        this.visibilityFriendList = visibilityFriendList;
        this.canComment = canComment;
        this.friendship = friendship;
        this.created_at = created_at;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public List<PostCommentDTO> getComments() {
        return comments;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public LocalDateTime getCreate_at() {
        return created_at;
    }

    public List<PostAttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<PostAttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public UserDTO getUser() {
        return user;
    }

    public PostPrivacySetting getPrivacySetting() {
        return privacySetting;
    }

    public CommentStatus getCommentStatus() {
        return commentStatus;
    }

    public Boolean getIsSensitive() {
        return isSensitive;
    }

    public List<FriendDTO> getVisibilityFriendList() {
        return visibilityFriendList;
    }

    public void setVisibilityFriendList(List<FriendDTO> visibilityFriendList) {
        this.visibilityFriendList = visibilityFriendList;
    }

    public boolean isCanComment() {
        return canComment;
    }

    public FriendshipDTO getFriendship() {
        return friendship;
    }
}
