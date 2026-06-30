package my_social_media_project_backend.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import my_social_media_project_backend.demo.dto.FriendStoriesDTO;
import my_social_media_project_backend.demo.dto.PaginatedResponseDTO;
import my_social_media_project_backend.demo.dto.StoryDTO;
import my_social_media_project_backend.demo.dto.StoryViewerDTO;
import my_social_media_project_backend.demo.dto.UserDTO;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.Media;
import my_social_media_project_backend.demo.entity.Story;
import my_social_media_project_backend.demo.entity.StoryView;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.mapper.FriendshipMapper;
import my_social_media_project_backend.demo.mapper.StoryMapper;
import my_social_media_project_backend.demo.mapper.UserMapper;
import my_social_media_project_backend.demo.repository.FriendshipRepository;
import my_social_media_project_backend.demo.repository.MediaRepository;
import my_social_media_project_backend.demo.repository.StoryRepository;
import my_social_media_project_backend.demo.repository.UserRepository;

@Service
@Transactional
public class StoryService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;
    private final FriendshipRepository friendshipRepository;
    private final StoryViewService storyViewService;

    public StoryService(
            StoryRepository storyRepository,
            UserRepository userRepository,
            MediaRepository mediaRepository,
            FriendshipRepository friendshipRepository,
            StoryViewService storyViewService
    ) {
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
        this.friendshipRepository = friendshipRepository;
        this.storyViewService = storyViewService;
    }

    public PaginatedResponseDTO<StoryDTO> getStories(
        Pageable pageable,
        Long userId,
        Long currentUserId,
        Boolean active,
        Long storyId
    ) {
        Page<StoryDTO> storyPage = storyRepository.getStories(userId, currentUserId, active, storyId, pageable)
            .map(story -> buildStoryDto(story, currentUserId));

        List<StoryDTO> storyDTOs = storyPage.getContent();

        return new PaginatedResponseDTO<>(
            storyDTOs,
            storyPage.getTotalElements(),
            (int) pageable.getOffset(),
            pageable.getPageSize()
        );
    }

    public StoryDTO getStoryDetails(Long storyId, Long currentUserId) {

        Story story = storyRepository.getStoryById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));

        return buildStoryDto(story, currentUserId);
    }

    public void viewStory(Long storyId, Long currentUserId) {

        Story story = storyRepository.getStoryById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));

        if (story.getUser().getId().equals(currentUserId)) {
            return;
        }

        storyViewService.viewStory(storyId, currentUserId);
    }

    public PaginatedResponseDTO<StoryViewerDTO> getStoryViewers(Long storyId, Long currentUserId, Pageable pageable) {
        Story story = storyRepository.getStoryById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));

        if (!story.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only the story owner can view story viewers");
        }

        List<StoryView> views = storyViewService.getStoryViews(storyId);

        List<StoryViewerDTO> storyViewerDTOs = views.stream()
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .map(view -> buildStoryViewerDto(view.getUser(), currentUserId))
            .toList();

        return new PaginatedResponseDTO<>(
            storyViewerDTOs,
            views.size(),
            (int) pageable.getOffset(),
            pageable.getPageSize()
        );
    }

    public PaginatedResponseDTO<FriendStoriesDTO> getFriendStories(
        Pageable pageable,
        Long currentUserId
    ) {
        Page<User> friendPage = friendshipRepository.getFriends(currentUserId, null, Friendship.FriendshipStatus.ACCEPTED, true, pageable);
        List<Long> friendIds = friendPage
            .getContent()
            .stream()
            .map(friend -> friend.getId())
            .toList();

        List<Long> userIds = new ArrayList<>();
        userIds.add(currentUserId);
        userIds.addAll(friendIds);

        List<Story> stories = storyRepository.getActiveStoriesByUserIds(userIds, currentUserId);

        Map<Long, List<Story>> storiesByUser = stories.stream()
            .collect(Collectors.groupingBy(
                s -> s.getUser().getId(),
                LinkedHashMap::new,
                Collectors.toList()
            ));

        List<FriendStoriesDTO> result = storiesByUser.entrySet().stream()
            .map(entry -> {
                List<StoryDTO> storyDTOs = entry.getValue()
                    .stream()
                    .map(story -> buildStoryDto(story, currentUserId))
                    .toList();

                UserDTO userDTO = UserMapper.toDto(
                    entry.getValue().get(0).getUser()
                );

                return new FriendStoriesDTO(userDTO, storyDTOs);
            })
            .toList();

        return new PaginatedResponseDTO<>(
            result,
            friendPage.getTotalElements(),
            (int) pageable.getOffset(),
            pageable.getPageSize()
        );
    }

    public List<StoryDTO> getActiveStoryDTOsByUserId(Long userId, Long currentUserId) {
        List<Story> stories = storyRepository.getActiveStoriesByUserIds(
            Collections.singletonList(userId),
            currentUserId
        );

        return stories.stream()
            .map(story -> buildStoryDto(story, currentUserId))
            .toList();
    }

    // =========================
    // CREATE STORY
    // =========================
    @Transactional
    public Long createStory(Long userId, Long mediaId, LocalDateTime expiresAt) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        LocalDateTime now = LocalDateTime.now();

        Story story = new Story();
        story.setUser(user);
        story.setMedia(media);
        story.setCreatedAt(now);
        story.setExpiresAt(expiresAt);

        Story createdStory = storyRepository.save(story);

        return createdStory.getId();
    }

    // =========================
    // GET ACTIVE STORIES (FEED)
    // =========================
    public List<Story> getActiveStories() {
        return storyRepository.findByExpiresAtAfter(LocalDateTime.now());
    }

    // =========================
    // GET USER STORIES
    // =========================
    public List<Story> getUserStories(Long userId) {
        return storyRepository.findByUserId(userId);
    }

    // =========================
    // DELETE STORY
    // =========================
    public void deleteStory(Long storyId) {
        storyRepository.deleteById(storyId);
    }

    // =========================
    // CLEANUP EXPIRED STORIES
    // (for scheduled job)
    // =========================
    public void deleteExpiredStories() {
        List<Story> expired = storyRepository.findByExpiresAtBefore(LocalDateTime.now());
        storyRepository.deleteAll(expired);
    }

    private StoryDTO buildStoryDto(Story story, Long currentUserId) {

        boolean isViewed = currentUserId != null
            && storyViewService.hasUserViewed(story.getId(), currentUserId);

        Long viewCount = null;
        if(Objects.equals(story.getUser().getId(), currentUserId)) {
            viewCount = storyViewService.countViews(story.getId());
        }

        return StoryMapper.toDto(story, isViewed, viewCount);
    }

    private StoryViewerDTO buildStoryViewerDto(User viewer, Long currentUserId) {
        Friendship friendship = friendshipRepository.findByUserAndFriendId(currentUserId, viewer.getId())
            .orElse(null);

        return new StoryViewerDTO(
            viewer.getId(),
            viewer.getUsername(),
            viewer.getUpdatedAt(),
            getActiveStoryDTOsByUserId(viewer.getId(), currentUserId),
            FriendshipMapper.toDto(friendship, currentUserId)
        );
    }
}
