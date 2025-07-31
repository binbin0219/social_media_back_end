package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;

public interface UserRecommendationDTO {
    Long getId();
    String getUsername();
    LocalDateTime getUpdatedAt();
}
