package my_social_media_project_backend.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "post_statistics")
public class PostStatistic {
    @Id
    private Integer postId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "like_count" , nullable = false)
    private long likeCount = 0;

    @Column(name = "comment_count" , nullable = false)
    private long commentCount = 0;

    public PostStatistic() {}

    public PostStatistic(Post post) {
        this.post = post;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }
}

