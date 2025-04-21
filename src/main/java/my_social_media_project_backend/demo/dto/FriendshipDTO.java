package my_social_media_project_backend.demo.dto;

import my_social_media_project_backend.demo.entity.Friendship;

import java.time.LocalDateTime;

public class FriendshipDTO {
    private final Long userId;
    private final Long friendId;
    private final Friendship.Status status;
    private final LocalDateTime createAt;

    public FriendshipDTO(Long userId, Long friendId, Friendship.Status status, LocalDateTime createAt) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
        this.createAt = createAt;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public Friendship.Status getStatus() {
        return status;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
}
