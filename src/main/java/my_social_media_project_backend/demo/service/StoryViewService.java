package my_social_media_project_backend.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import my_social_media_project_backend.demo.entity.Story;
import my_social_media_project_backend.demo.entity.StoryView;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.repository.StoryRepository;
import my_social_media_project_backend.demo.repository.StoryViewRepository;
import my_social_media_project_backend.demo.repository.UserRepository;

@Service
@Transactional
public class StoryViewService {

    private final StoryViewRepository storyViewRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

    public StoryViewService(
            StoryViewRepository storyViewRepository,
            StoryRepository storyRepository,
            UserRepository userRepository
    ) {
        this.storyViewRepository = storyViewRepository;
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // MARK STORY AS VIEWED
    // =========================
    public StoryView viewStory(Long storyId, Long userId) {

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // prevent duplicate views (because of unique constraint)
        return storyViewRepository.findByStoryIdAndUserId(storyId, userId)
                .orElseGet(() -> {
                    StoryView view = new StoryView();
                    view.setStory(story);
                    view.setUser(user);
                    view.setViewedAt(LocalDateTime.now());

                    return storyViewRepository.save(view);
                });
    }

    // =========================
    // GET STORY VIEWS
    // =========================
    public List<StoryView> getStoryViews(Long storyId) {
        return storyViewRepository.findByStoryId(storyId);
    }

    // =========================
    // GET USER VIEW HISTORY
    // =========================
    public List<StoryView> getUserViewHistory(Long userId) {
        return storyViewRepository.findByUserId(userId);
    }

    // =========================
    // CHECK IF VIEWED
    // =========================
    public boolean hasUserViewed(Long storyId, Long userId) {
        return storyViewRepository.findByStoryIdAndUserId(storyId, userId).isPresent();
    }

    // =========================
    // COUNT STORY VIEWS
    // =========================
    public long countViews(Long storyId) {
        return storyViewRepository.countByStoryId(storyId);
    }

    // =========================
    // DELETE VIEWS (OPTIONAL)
    // =========================
    public void deleteViewsByStory(Long storyId) {
        storyViewRepository.deleteByStoryId(storyId);
    }
}