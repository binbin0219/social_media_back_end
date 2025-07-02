package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDTO {
    private final Long id;
    private final String title;
    private final String content;
    private final List<PostCommentDTO> comments = new ArrayList<>();
    private final Long likeCount;
    private final Long commentCount;
    private final boolean isLiked;
    private List<PostAttachmentDTO> attachments;
    private final LocalDateTime create_at;

    public PostDTO(Long id, String title, String content, Long likeCount, Long commentCount, boolean isLiked, List<PostAttachmentDTO> attachments, LocalDateTime create_at) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLiked = isLiked;
        this.attachments = attachments;
        this.create_at = create_at;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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
        return create_at;
    }

    public List<PostAttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<PostAttachmentDTO> attachments) {
        this.attachments = attachments;
    }
}
