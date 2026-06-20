package my_social_media_project_backend.demo.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Size;
import my_social_media_project_backend.demo.enums.CommentStatus;
import my_social_media_project_backend.demo.enums.PostPrivacySetting;

public class PostCreateDTO {

    @Size(max = 2500 , message = "Content cannot exceed 2500 letters")
    private String content;

    private PostPrivacySetting privacySetting = PostPrivacySetting.PUBLIC;
    private CommentStatus commentStatus = CommentStatus.OPEN;
    private Boolean isSensitive = false;
    private List<Long> selectedFriendIds = new ArrayList<>();

    private List<MultipartFile> attachments  = new ArrayList<>();

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<MultipartFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MultipartFile> attachments) {
        this.attachments = attachments;
    }

    public PostPrivacySetting getPrivacySetting() {
        return privacySetting;
    }

    public void setPrivacySetting(PostPrivacySetting privacySetting) {
        this.privacySetting = privacySetting;
    }

    public CommentStatus getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(CommentStatus commentStatus) {
        this.commentStatus = commentStatus;
    }

    public Boolean getIsSensitive() {
        return isSensitive;
    }

    public void setIsSensitive(Boolean isSensitive) {
        this.isSensitive = isSensitive;
    }

    public List<Long> getSelectedFriendIds() {
        return selectedFriendIds;
    }

    public void setSelectedFriendIds(List<Long> selectedFriendIds) {
        this.selectedFriendIds = selectedFriendIds;
    }
}
