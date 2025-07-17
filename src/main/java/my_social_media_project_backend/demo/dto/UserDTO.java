package my_social_media_project_backend.demo.dto;

import com.fasterxml.jackson.databind.JsonNode;
import my_social_media_project_backend.demo.utility.JsonUtil;

import java.time.LocalDateTime;

public class UserDTO <T> {
    private Long id;
    private String country;
    private String username;
    private String firstName;
    private String lastName;
    private String description;
    private String occupation;
    private JsonNode phoneNumber;
    private String region;
    private String relationshipStatus;
    private String gender;
    private String avatar;
    private String coverUrl;
    private FriendshipDTO friendship;
    private Long friendCount;
    private Long newNotificationCount;
    private Long unreadChatMessageCount;
    private Long postCount;
    private Long likeCount;
    private LocalDateTime createAt;

    public UserDTO(
            Long id,
            String country,
            String username,
            String firstName,
            String lastName,
            String description,
            String occupation,
            T phoneNumber,
            String region,
            String relationshipStatus,
            String gender,
            String avatar,
            String coverUrl,
            FriendshipDTO friendship,
            Long friendCount,
            Long newNotificationCount,
            Long unreadChatMessageCount,
            Long postCount,
            Long likeCount,
            LocalDateTime createAt
    ) {
        this.id = id;
        this.country = country;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.occupation = occupation;
        this.region = region;
        this.relationshipStatus = relationshipStatus;
        this.gender = gender;
        this.avatar = avatar;
        this.coverUrl = coverUrl;
        this.friendship = friendship;
        this.friendCount = friendCount;
        this.newNotificationCount = newNotificationCount;
        this.unreadChatMessageCount = unreadChatMessageCount;
        this.postCount = postCount;
        this.likeCount = likeCount;
        this.createAt = createAt;

        if(phoneNumber != null) {
            this.phoneNumber = (phoneNumber instanceof JsonNode)
                    ? (JsonNode) phoneNumber
                    : JsonUtil.convertStringToJsonNode((String) phoneNumber);
        } else {
            this.phoneNumber = null;
        }
    }

    public Long getId() {
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

    public Long getFriendCount() {
        return friendCount;
    }

    public Long getNewNotificationCount() {
        return newNotificationCount;
    }

    public Long getUnreadChatMessageCount() {
        return unreadChatMessageCount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setPhoneNumber(JsonNode phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public void setFriendship(FriendshipDTO friendship) {
        this.friendship = friendship;
    }

    public void setFriendCount(long friendCount) {
        this.friendCount = friendCount;
    }

    public void setNewNotificationCount(long newNotificationCount) {
        this.newNotificationCount = newNotificationCount;
    }

    public void setUnreadChatMessageCount(Long unreadChatMessageCount) {
        this.unreadChatMessageCount = unreadChatMessageCount;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPostCount() {
        return postCount;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
}
