package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDTO {
    private final Integer id;
    private final String title;
    private final String content;
    private final List<PostCommentDTO> comments = new ArrayList<>();
    private final long likeCount;
    private final long commentCount;
    private final boolean isLiked;
    private final LocalDateTime create_at;

    public PostDTO(Integer id, String title, String content, long likeCount, long commentCount, boolean isLiked, LocalDateTime create_at) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLiked = isLiked;
        this.create_at = create_at;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public List<PostCommentDTO> getComments() {
        return comments;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public LocalDateTime getCreate_at() {
        return create_at;
    }
}
