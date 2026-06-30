package my_social_media_project_backend.demo.mapper;

import my_social_media_project_backend.demo.dto.StoryDTO;
import my_social_media_project_backend.demo.entity.Story;

public class StoryMapper {

    private StoryMapper() {}

    public static StoryDTO toDto(Story story, boolean isViewed) {
        return toDto(story, isViewed, 0L);
    }

    public static StoryDTO toDto(Story story, boolean isViewed, Long viewCount) {

        if (story == null) {
            return null;
        }

        return new StoryDTO(
            story.getId(),

            UserMapper.toDto(
                story.getUser(),
                null,
                null,
                null,
                null,
                null,
                null
            ),

            MediaMapper.toDto(
                story.getMedia()
            ),

            isViewed,
            viewCount,

            story.getCreatedAt(),
            story.getExpiresAt()
        );
    }
}
