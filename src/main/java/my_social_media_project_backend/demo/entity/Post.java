package my_social_media_project_backend.demo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import my_social_media_project_backend.demo.enums.CommentStatus;
import my_social_media_project_backend.demo.enums.PostPrivacySetting;
import my_social_media_project_backend.demo.enums.PostType;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_post_id")
    private Post sharedPost;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    private PostType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_status")
    private CommentStatus commentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy_setting")
    private PostPrivacySetting privacySetting = PostPrivacySetting.PUBLIC;

    @Column(nullable = false)
    private Integer seq = 0;

    @Column(name = "is_subscription_only", nullable = false)
    private Integer isSubscriptionOnly = 0;

    @Column(name = "is_sensitive_content", nullable = false)
    private Integer isSensitiveContent = 0;

    @Column(name = "created_at")
    private final LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostAttachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostVisibilityAllow> visibilityAllows = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostVisibilityDeny> visibilityDenys = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private PostStatistic postStatistic;

    @OneToOne(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Poll poll;

    @OneToMany(mappedBy = "sharedPost")
    private List<Post> reposts = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreateAt() {
        return createdAt;
    }

    public List<PostLike> getPostLikes() {
        return postLikes;
    }

    public void setPostLikes(List<PostLike> postLikes) {
        this.postLikes = postLikes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<PostAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<PostAttachment> attachments) {
        this.attachments = attachments;
    }

    public PostStatistic getPostStatistic() {
        return postStatistic;
    }

    public void setPostStatistic(PostStatistic postStatistic) {
        this.postStatistic = postStatistic;
    }

    public PostType getType() {
        return type;
    }

    public void setType(PostType type) {
        this.type = type;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public int getIsSubscriptionOnly() {
        return isSubscriptionOnly;
    }
    public void setIsSubscriptionOnly(int isSubscriptionOnly) {
        this.isSubscriptionOnly = isSubscriptionOnly;
    }

    public int getIsSensitiveContent() {
        return isSensitiveContent;
    }

    public void setIsSensitiveContent(int isSensitiveContent) {
        this.isSensitiveContent = isSensitiveContent;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public CommentStatus getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(CommentStatus commentStatus) {
        this.commentStatus = commentStatus;
    }

    public PostPrivacySetting getPrivacySetting() {
        return privacySetting;
    }

    public void setPrivacySetting(PostPrivacySetting privacySetting) {
        this.privacySetting = privacySetting;
    }

    public List<PostVisibilityDeny> getVisibilityDenys() {
        return visibilityDenys;
    }

    public void setVisibilityDenys(List<PostVisibilityDeny> visibilityDenys) {
        this.visibilityDenys = visibilityDenys;
    }

    public List<PostVisibilityAllow> getVisibilityAllows() {
        return visibilityAllows;
    }

    public void setVisibilityAllows(List<PostVisibilityAllow> visibilityAllows) {
        this.visibilityAllows = visibilityAllows;
    }

    public Post getSharedPost() {
        return sharedPost;
    }

    public void setSharedPost(Post sharedPost) {
        this.sharedPost = sharedPost;
    }

    public List<Post> getReposts() {
        return reposts;
    }

    public void setReposts(List<Post> reposts) {
        this.reposts = reposts;
    }
}
