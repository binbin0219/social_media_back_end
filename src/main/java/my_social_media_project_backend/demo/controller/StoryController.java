package my_social_media_project_backend.demo.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.FriendStoriesDTO;
import my_social_media_project_backend.demo.dto.PaginatedResponseDTO;
import my_social_media_project_backend.demo.dto.StoryDTO;
import my_social_media_project_backend.demo.dto.StoryViewerDTO;
import my_social_media_project_backend.demo.service.StoryService;

@RestController
@RequestMapping("api/story")
@Validated
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping("/friends")
    public ResponseEntity<PaginatedResponseDTO<FriendStoriesDTO>> getFriendStories(
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") Integer size
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
            storyService.getFriendStories(pageable, userDetails.getUserId())
        );
    }

    @PostMapping("/")
    public ResponseEntity<StoryDTO> createStory(
        @RequestParam Long mediaId
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        return ResponseEntity.status(HttpStatus.CREATED).body(
            storyService.getStoryDetails(storyService.createStory(userDetails.getUserId(), mediaId, LocalDateTime.now().plusHours(24)), userDetails.getUserId())
        );
    }

    @PostMapping("/{storyId}/view")
    public ResponseEntity<Void> viewStory(@PathVariable Long storyId) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        storyService.viewStory(storyId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{storyId}/viewers")
    public ResponseEntity<PaginatedResponseDTO<StoryViewerDTO>> getStoryViewers(
        @PathVariable Long storyId,
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") Integer size
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
            storyService.getStoryViewers(storyId, userDetails.getUserId(), pageable)
        );
    }

    @DeleteMapping("/{storyId}")
    public ResponseEntity<Void> deleteStory(@PathVariable Long storyId) {
        storyService.deleteStory(storyId);
        return ResponseEntity.noContent().build();
    }
}
