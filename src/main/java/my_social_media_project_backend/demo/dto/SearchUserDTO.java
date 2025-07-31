package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;

public class SearchUserDTO {
    private Long id;
    private String username;
    private LocalDateTime updatedAt;

    public SearchUserDTO(Long id, String username, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
