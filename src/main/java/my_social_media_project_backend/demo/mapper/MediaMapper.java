package my_social_media_project_backend.demo.mapper;

import my_social_media_project_backend.demo.dto.MediaDTO;
import my_social_media_project_backend.demo.entity.Media;

public class MediaMapper {

    private MediaMapper() {}

    public static MediaDTO toDto(Media media) {

        if (media == null) {
            return null;
        }

        return new MediaDTO(
            media.getId(),
            media.getUrl(),
            media.getMimeType(),
            media.getSize(),
            media.getCreatedAt()
        );
    }
}