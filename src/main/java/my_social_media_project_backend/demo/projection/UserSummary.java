package my_social_media_project_backend.demo.projection;

import java.time.LocalDateTime;

public interface UserSummary {
    Long getId();
    String getUsername();
    LocalDateTime getUpdatedAt();
}

