package my_social_media_project_backend.demo.dto;

public class FriendDTO {
    private long id;
    private String avatar;
    private String username;

    public FriendDTO(long id, String avatar, String username) {
        this.id = id;
        this.avatar = avatar;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
