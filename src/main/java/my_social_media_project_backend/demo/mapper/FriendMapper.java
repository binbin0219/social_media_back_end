package my_social_media_project_backend.demo.mapper;

import my_social_media_project_backend.demo.dto.FriendDTO;
import my_social_media_project_backend.demo.entity.User;

public class FriendMapper {

    private FriendMapper() {
        // Prevent instantiation
    }

    public static FriendDTO toDto(User friend) {

        if (friend == null) {
            return null;
        }

        return new FriendDTO(
                friend.getId(),
                friend.getUsername(),
                friend.getUpdatedAt()
        );
    }
}