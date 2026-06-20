package my_social_media_project_backend.demo.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import my_social_media_project_backend.demo.enums.CommentStatus;
import my_social_media_project_backend.demo.enums.PostPrivacySetting;

public class SharePostRequest {

    @NotNull(message = "originalPostId is required")
    private Long originalPostId;

    // Optional caption the sharer adds on top
    private String content;

    private PostPrivacySetting privacySetting = PostPrivacySetting.PUBLIC;

    private CommentStatus commentStatus = CommentStatus.OPEN;

    private Boolean isSensitive = false;

    // Only needed for WCV / WCNV privacy settings
    private List<Long> selectedFriendIds = List.of();

    // ── getters / setters ──────────────────────────────────────────────────

    public Long getOriginalPostId()                      { return originalPostId; }
    public void setOriginalPostId(Long originalPostId)   { this.originalPostId = originalPostId; }

    public String getContent()                           { return content; }
    public void setContent(String content)               { this.content = content; }

    public PostPrivacySetting getPrivacySetting()        { return privacySetting; }
    public void setPrivacySetting(PostPrivacySetting p)  { this.privacySetting = p; }

    public CommentStatus getCommentStatus()              { return commentStatus; }
    public void setCommentStatus(CommentStatus c)        { this.commentStatus = c; }

    public Boolean getIsSensitive()                      { return isSensitive; }
    public void setIsSensitive(Boolean isSensitive)      { this.isSensitive = isSensitive; }

    public List<Long> getSelectedFriendIds()             { return selectedFriendIds; }
    public void setSelectedFriendIds(List<Long> ids)     { this.selectedFriendIds = ids; }
}