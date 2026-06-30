package my_social_media_project_backend.demo.dto;

import java.util.List;

public class FriendStoriesDTO {
    private final UserDTO user;
    private final List<StoryDTO> stories;

    public FriendStoriesDTO(UserDTO user, List<StoryDTO> stories) {
        this.user = user;
        this.stories = stories;
    }

    public UserDTO getUser() { return user; }
    public List<StoryDTO> getStories() { return stories; }
}