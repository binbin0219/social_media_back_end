package my_social_media_project_backend.demo.dto;

import java.time.LocalDateTime;

public class MediaDTO {

    private final Long id;
    private final String url;
    private final String mimeType;
    private final Long size;
    private final LocalDateTime createdAt;

    public MediaDTO(
        Long id,
        String url,
        String mimeType,
        Long size,
        LocalDateTime createdAt
    ) {
        this.id = id;
        this.url = url;
        this.mimeType = mimeType;
        this.size = size;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Long getSize() {
        return size;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}