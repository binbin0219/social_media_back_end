package my_social_media_project_backend.demo.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import my_social_media_project_backend.demo.dto.PostLikeDTO;
import my_social_media_project_backend.demo.entity.Notification;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.PostLike;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.repository.PostLikeRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostStatisticsService postStatisticsService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserStatisticService userStatisticService;

    public PostLikeService(PostLikeRepository postLikeRepository, PostStatisticsService postStatisticsService, NotificationService notificationService, SimpMessagingTemplate messagingTemplate, UserStatisticService userStatisticService) {
        this.postLikeRepository = postLikeRepository;
        this.postStatisticsService = postStatisticsService;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
        this.userStatisticService = userStatisticService;
    }

    public PostLike likePost(Post post, User user) {
        PostLike existingPostLike = postLikeRepository.findByPostAndUser(post, user).orElse(null);
        if(existingPostLike != null) throw new EntityExistsException("User " + user.getId() + " has already liked this post (" + post.getId() + ")");
        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setUser(user);
        PostLike savedPostLike = postLikeRepository.save(postLike);
        postStatisticsService.incrementLikeCount(post.getId());
        userStatisticService.incrementLikeCount(post.getUser().getId());

        if(!isLikeByAuthor(post.getUser().getId(), user.getId())) {
            notificationService.sendNotification(
                    user,
                    post.getUser(),
                    Notification.Type.LIKE,
                    post.getTitle(),
                    null,
                    post.getId()
            );
        }

        messagingTemplate.convertAndSend(
                "/topic/" + post.getId() + "/postLikes",
                ""
        );

        return savedPostLike;
    }

    public void unlikePost(Post post, User user) {
        PostLike postLike = postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(()-> new EntityNotFoundException("Post like not found"));
        postLikeRepository.delete(postLike);
        postStatisticsService.decrementLikeCount(post.getId());
        userStatisticService.decrementLikeCount(post.getUser().getId());

        if(!isLikeByAuthor(post.getUser().getId(), user.getId())) {
            // Delete the like notification on post author side
            notificationService.deleteByTargetIdAndType(
                    user.getId(),
                    post.getUser().getId(),
                    post.getId(),
                    Notification.Type.LIKE
            );
        }

        messagingTemplate.convertAndSend(
                "/topic/" + post.getId() + "/postDislikes",
                ""
        );
    }

    public List<PostLikeDTO> convertToPostLikeDTOs(List<PostLike> postLikes) {
        return postLikes.stream()
                .map(postLike -> new PostLikeDTO(postLike.getUserId()))
                .collect(Collectors.toList());
    }

    private boolean isLikeByAuthor(Long postAuthorId, Long userId) {
        return Objects.equals(postAuthorId, userId);
    }
}
