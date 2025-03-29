package my_social_media_project_backend.demo.dto;

import com.fasterxml.jackson.databind.JsonNode;
import my_social_media_project_backend.demo.entity.Notification;
import my_social_media_project_backend.demo.utility.JsonUtil;

import java.time.LocalDateTime;
import java.util.List;

public class UserDTO <T> {
    private final Integer id;
    private final String country;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String occupation;
    private final JsonNode phoneNumber;
    private final String region;
    private final String relationshipStatus;
    private final String gender;
    private final String avatar;
    private final String coverUrl;
    private final FriendshipDTO friendship;
    private final long friendCount;
    private final long unseenNotificationCount;
    private final LocalDateTime createAt;

    public UserDTO(
            Integer id,
            String country,
            String username,
            String firstName,
            String lastName,
            String occupation,
            T phoneNumber,
            String region,
            String relationshipStatus,
            String gender,
            String avatar,
            String coverUrl,
            FriendshipDTO friendship,
            long friendCount,
            long unseenNotificationCount,
            LocalDateTime createAt
    ) {
        this.id = id;
        this.country = country;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.occupation = occupation;
        this.region = region;
        this.relationshipStatus = relationshipStatus;
        this.gender = gender;
        this.avatar = avatar;
        this.coverUrl = coverUrl;
        this.friendship = friendship;
        this.friendCount = friendCount;
        this.unseenNotificationCount = unseenNotificationCount;
        this.createAt = createAt;

        if(phoneNumber != null) {
            this.phoneNumber = (phoneNumber instanceof JsonNode)
                    ? (JsonNode) phoneNumber
                    : JsonUtil.convertStringToJsonNode((String) phoneNumber);
        } else {
            this.phoneNumber = null;
        }
    }

    public Integer getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getOccupation() {
        return occupation;
    }

    public JsonNode getPhoneNumber() {
        return phoneNumber;
    }

    public String getRegion() {
        return region;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public String getGender() {
        return gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public FriendshipDTO getFriendship() {
        return friendship;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public long getFriendCount() {
        return friendCount;
    }

    public long getUnseenNotificationCount() {
        return unseenNotificationCount;
    }
}
