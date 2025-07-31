package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;

public class FriendDTO {
    private long id;
    private String username;
    private LocalDateTime updatedAt;

    public FriendDTO(long id, String username, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
