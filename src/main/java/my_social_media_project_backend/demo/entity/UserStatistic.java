package my_social_media_project_backend.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_statistics")
public class UserStatistic {
    @Id
    private Long userId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "unseen_notification_count" , nullable = false)
    private Long unseenNotificationCount = 0L;

    @Column(name = "seen_notification_count" , nullable = false)
    private Long seenNotificationCount = 0L;

    @Column(name = "new_notification_count")
    private Long newNotificationCount = 0L;

    @Column(name = "friend_count" , nullable = false)
    private Long friendCount = 0L;

    @Column(name = "post_count", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long postCount = 0L;

    @Column(name = "like_count", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long likeCount = 0L;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUnseenNotificationCount() {
        return unseenNotificationCount;
    }

    public void setUnseenNotificationCount(Long unseenNotificationCount) {
        this.unseenNotificationCount = unseenNotificationCount;
    }

    public Long getSeenNotificationCount() {
        return seenNotificationCount;
    }

    public void setSeenNotificationCount(Long seenNotificationCount) {
        this.seenNotificationCount = seenNotificationCount;
    }

    public Long getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(Long friendCount) {
        this.friendCount = friendCount;
    }

    public Long getNewNotificationCount() {
        return newNotificationCount;
    }

    public void setNewNotificationCount(Long newNotificationCount) {
        this.newNotificationCount = newNotificationCount;
    }

    public Long getPostCount() {
        return postCount;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
}
