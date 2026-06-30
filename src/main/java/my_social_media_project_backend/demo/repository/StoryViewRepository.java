package my_social_media_project_backend.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import my_social_media_project_backend.demo.entity.StoryView;

@Repository
public interface StoryViewRepository extends JpaRepository<StoryView, Long> {

    // Get all views for a story
    List<StoryView> findByStoryId(Long storyId);

    // Get all stories viewed by a user
    List<StoryView> findByUserId(Long userId);

    // Check if a user already viewed a story (important for UX)
    Optional<StoryView> findByStoryIdAndUserId(Long storyId, Long userId);

    // Count views for a story (for analytics)
    long countByStoryId(Long storyId);

    // Delete views for a story (if needed manually)
    void deleteByStoryId(Long storyId);
}