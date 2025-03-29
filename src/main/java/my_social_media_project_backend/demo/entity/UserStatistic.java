package my_social_media_project_backend.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_statistics")
public class UserStatistic {
    @Id
    private Integer userId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "unseen_notification_count" , nullable = false)
    private long unseenNotificationCount = 0;

    @Column(name = "seen_notification_count" , nullable = false)
    private long seenNotificationCount = 0;

    @Column(name = "friend_count" , nullable = false)
    private long friendCount = 0;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getUnseenNotificationCount() {
        return unseenNotificationCount;
    }

    public void setUnseenNotificationCount(long unseenNotificationCount) {
        this.unseenNotificationCount = unseenNotificationCount;
    }

    public long getSeenNotificationCount() {
        return seenNotificationCount;
    }

    public void setSeenNotificationCount(long seenNotificationCount) {
        this.seenNotificationCount = seenNotificationCount;
    }

    public long getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(long friendCount) {
        this.friendCount = friendCount;
    }
}
