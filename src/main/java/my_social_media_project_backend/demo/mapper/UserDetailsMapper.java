package my_social_media_project_backend.demo.mapper;

import com.fasterxml.jackson.databind.JsonNode;

import my_social_media_project_backend.demo.dto.FriendshipDTO;
import my_social_media_project_backend.demo.dto.UserDetailsDTO;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.utility.JsonUtil;

public class UserDetailsMapper {

    private UserDetailsMapper() {}

    // ✅ SIMPLE VERSION (what you want most of the time)
    public static UserDetailsDTO<JsonNode> toDto(User user) {
        return toDto(user, null, null, null, null, null, null);
    }

    // ✅ FULL VERSION (optional enrichment)
    public static UserDetailsDTO<JsonNode> toDto(
            User user,
            FriendshipDTO friendship,
            Long friendCount,
            Long newNotificationCount,
            Long unreadChatMessageCount,
            Long postCount,
            Long likeCount
    ) {
        if (user == null) return null;

        JsonNode phoneJson = null;

        if (user.getPhoneNumber() != null) {
            phoneJson = JsonUtil.convertObjectToJsonNode(user.getPhoneNumber());
        }

        return new UserDetailsDTO<>(
                user.getId(),
                user.getCountry(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getDescription(),
                user.getOccupation(),
                phoneJson,
                user.getRegion(),
                user.getRelationshipStatus(),
                user.getGender(),
                friendship,
                friendCount,
                newNotificationCount,
                unreadChatMessageCount,
                postCount,
                likeCount,
                user.getCreateAt(),
                user.getUpdatedAt()
        );
    }
}