package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;

import my_social_media_project_backend.demo.entity.Friendship;

public class FriendshipDTO {
    private final Long userId;
    private final Long friendId;
    private final Friendship.FriendshipStatus status;
    private final boolean isSender;
    private final LocalDateTime createdAt;

    public FriendshipDTO(Long userId, Long friendId, Friendship.FriendshipStatus status, boolean isSender, LocalDateTime createdAt) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
        this.isSender = isSender;
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public Friendship.FriendshipStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreateAt() {
        return createdAt;
    }

    public boolean isIsSender() {
        return isSender;
    }
}
