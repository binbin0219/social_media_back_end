package my_social_media_project_backend.demo.dto;

import my_social_media_project_backend.demo.entity.Friendship;

import java.time.LocalDateTime;

public class FriendshipDTO {
    private final Integer userId;
    private final Integer friendId;
    private final Friendship.Status status;
    private final LocalDateTime createAt;

    public FriendshipDTO(Integer userId, Integer friendId, Friendship.Status status, LocalDateTime createAt) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
        this.createAt = createAt;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getFriendId() {
        return friendId;
    }

    public Friendship.Status getStatus() {
        return status;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
}
