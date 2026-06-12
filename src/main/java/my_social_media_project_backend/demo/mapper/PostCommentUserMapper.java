package my_social_media_project_backend.demo.mapper;

import my_social_media_project_backend.demo.dto.FriendshipDTO;
import my_social_media_project_backend.demo.dto.PostCommentUserDTO;
import my_social_media_project_backend.demo.entity.User;

public class PostCommentUserMapper {

    public static PostCommentUserDTO toDto(User user, FriendshipDTO friendship) {
        if (user == null) {
            return null;
        }

        return new PostCommentUserDTO(
                user.getId(),
                user.getUsername(),
                friendship,
                user.getUpdatedAt()
        );
    }
}