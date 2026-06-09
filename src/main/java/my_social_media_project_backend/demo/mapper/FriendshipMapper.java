package my_social_media_project_backend.demo.mapper;

import java.util.Objects;

import my_social_media_project_backend.demo.dto.FriendshipDTO;
import my_social_media_project_backend.demo.entity.Friendship;

public class FriendshipMapper {

    private FriendshipMapper() {
        // Prevent instantiation
    }

    public static FriendshipDTO toDto(Friendship friendship, Long currentUserId) {

        if (friendship == null) {
            return null;
        }

        return new FriendshipDTO(
                friendship.getUser().getId(),
                friendship.getFriend().getId(),
                friendship.getStatus(), 
                Objects.equals(friendship.getUser().getId(), currentUserId),
                friendship.getCreateAt()
        );
    }
}