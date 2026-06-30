package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UserDTO <T> {
    private Long id;
    private String country;
    private String username;
    private String firstName;
    private String lastName;
    private String description;
    private String gender;
    private FriendshipDTO friendship;
    private Long friendCount;
    private Long postCount;
    private Long likeCount;
    private List<StoryDTO> stories;
    private LocalDateTime updatedAt;

    public UserDTO(
            Long id,
            String country,
            String username,
            String firstName,
            String lastName,
            String description,
            String gender,
            FriendshipDTO friendship,
            Long friendCount,
            Long postCount,
            Long likeCount,
            List<StoryDTO> stories,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.country = country;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.gender = gender;
        this.friendship = friendship;
        this.friendCount = friendCount;
        this.postCount = postCount;
        this.likeCount = likeCount;
        this.stories = stories;
        this.updatedAt = updatedAt;
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

    public String getGender() {
        return gender;
    }

    public FriendshipDTO getFriendship() {
        return friendship;
    }

    public Long getFriendCount() {
        return friendCount;
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

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setFriendship(FriendshipDTO friendship) {
        this.friendship = friendship;
    }

    public void setFriendCount(long friendCount) {
        this.friendCount = friendCount;
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

    public void setFriendCount(Long friendCount) {
        this.friendCount = friendCount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<StoryDTO> getStories() {
        return stories;
    }

    public void setStories(List<StoryDTO> stories) {
        this.stories = stories;
    }
}
