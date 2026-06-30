package my_social_media_project_backend.demo.mapper;

import java.util.List;

import my_social_media_project_backend.demo.dto.StoryDTO;
import my_social_media_project_backend.demo.dto.UserDTO;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.User;

public class UserMapper {

    private UserMapper() {}

    public static UserDTO toDto(User user) {
        return toDto(user, null);
    }

    public static UserDTO toDto(User user, List<StoryDTO> stories) {
        return toDto(user, null, null, null, null, null, stories);
    }

    public static UserDTO toDto(User user, Friendship friendship, List<StoryDTO> stories) {
        return toDto(user, friendship, null, null, null, null, stories);
    }

    public static UserDTO toDto(
            User user,
            Friendship friendship,
            Long friendCount,
            Long postCount,
            Long likeCount,
            Long currentUserId
    ) {
        return toDto(user, friendship, friendCount, postCount, likeCount, currentUserId, null);
    }

    public static UserDTO toDto(
            User user,
            Friendship friendship,
            Long friendCount,
            Long postCount,
            Long likeCount,
            Long currentUserId,
            List<StoryDTO> stories
    ) {
        if (user == null) return null;

        return new UserDTO<>(
                user.getId(),
                user.getCountry(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getDescription(),
                user.getGender(),

                FriendshipMapper.toDto(friendship, currentUserId),
                friendCount,
                postCount,
                likeCount,
                stories,

                user.getUpdatedAt()
        );
    }
}
