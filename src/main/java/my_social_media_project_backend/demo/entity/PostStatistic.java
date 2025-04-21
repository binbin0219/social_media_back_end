package my_social_media_project_backend.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "post_statistics")
public class PostStatistic {
    @Id
    private Long postId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "like_count" , nullable = false)
    private Long likeCount = 0L;

    @Column(name = "comment_count" , nullable = false)
    private Long commentCount = 0L;

    public PostStatistic() {}

    public PostStatistic(Post post) {
        this.post = post;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
}

