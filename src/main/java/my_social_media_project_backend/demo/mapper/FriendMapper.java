package my_social_media_project_backend.demo.mapper;

import java.util.List;

import my_social_media_project_backend.demo.dto.FriendDTO;
import my_social_media_project_backend.demo.dto.StoryDTO;
import my_social_media_project_backend.demo.entity.User;

public class FriendMapper {

    private FriendMapper() {
        // Prevent instantiation
    }

    public static FriendDTO toDto(User friend) {
        return toDto(friend, null);
    }

    public static FriendDTO toDto(User friend, List<StoryDTO> stories) {

        if (friend == null) {
            return null;
        }

        return new FriendDTO(
                friend.getId(),
                friend.getUsername(),
                stories,
                friend.getUpdatedAt()
        );
    }
}
