package my_social_media_project_backend.demo.mapper;

import java.util.List;

import my_social_media_project_backend.demo.dto.FriendshipDTO;
import my_social_media_project_backend.demo.dto.PostCommentUserDTO;
import my_social_media_project_backend.demo.dto.StoryDTO;
import my_social_media_project_backend.demo.entity.User;

public class PostCommentUserMapper {

    public static PostCommentUserDTO toDto(User user, FriendshipDTO friendship) {
        return toDto(user, friendship, null);
    }

    public static PostCommentUserDTO toDto(User user, FriendshipDTO friendship, List<StoryDTO> stories) {
        if (user == null) {
            return null;
        }

        return new PostCommentUserDTO(
                user.getId(),
                user.getUsername(),
                friendship,
                stories,
                user.getUpdatedAt()
        );
    }
}
