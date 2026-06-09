package my_social_media_project_backend.demo.mapper;

import my_social_media_project_backend.demo.dto.UserDTO;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.User;

public class UserMapper {

    private UserMapper() {}

    public static UserDTO toDto(User user) {
        return toDto(user, null, null, null, null, null);
    }

    public static UserDTO toDto(
            User user,
            Friendship friendship,
            Long friendCount,
            Long postCount,
            Long likeCount,
            Long currentUserId
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

                user.getUpdatedAt()
        );
    }
}