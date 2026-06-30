package my_social_media_project_backend.demo.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import my_social_media_project_backend.demo.dto.PaginatedResponseDTO;
import my_social_media_project_backend.demo.dto.PostCommentDTO;
import my_social_media_project_backend.demo.dto.PostCommentUserDTO;
import my_social_media_project_backend.demo.dto.StoryDTO;
import my_social_media_project_backend.demo.entity.Comment;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.enums.CommentStatus;
import my_social_media_project_backend.demo.enums.NotificationType;
import my_social_media_project_backend.demo.exception.CommentNotAllowedException;
import my_social_media_project_backend.demo.mapper.FriendshipMapper;
import my_social_media_project_backend.demo.mapper.PostCommentMapper;
import my_social_media_project_backend.demo.mapper.PostCommentUserMapper;
import my_social_media_project_backend.demo.repository.CommentRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostStatisticsService postStatisticsService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FriendshipService friendshipService;
    private final StoryService storyService;

    public CommentService(
        CommentRepository commentRepository, 
        PostStatisticsService postStatisticsService, 
        NotificationService notificationService, 
        SimpMessagingTemplate messagingTemplate,
        FriendshipService friendshipService,
        StoryService storyService
    ) {
        this.commentRepository = commentRepository;
        this.postStatisticsService = postStatisticsService;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
        this.friendshipService = friendshipService;
        this.storyService = storyService;
    }

    public PostCommentDTO create(Post post, User user, String content) {

        // 1. CLOSED: no comments allowed at all
        if (post.getCommentStatus() == CommentStatus.CLOSED) {
            throw new CommentNotAllowedException("Comments are closed for this post.");
        }

        boolean isAuthor = Objects.equals(post.getUser().getId(), user.getId());

        // 2. ONLY_FRIENDS: only friends OR author allowed
        if (post.getCommentStatus() == CommentStatus.ONLY_FRIENDS && !isAuthor) {
            boolean isFriend = friendshipService.checkIsFriend(
                    post.getUser().getId(),
                    user.getId()
            );

            if (!isFriend) {
                throw new CommentNotAllowedException("Only friends can comment on this post.");
            }
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);
        commentRepository.save(comment);

        postStatisticsService.incrementCommentCount(post.getId());

        if (!isPostAuthorCommentOnOwnPost(post.getUser().getId(), user.getId())) {
            notificationService.sendNotification(
                    user,
                    post.getUser(),
                    NotificationType.COMMENT,
                    post.getContent(),
                    null,
                    post.getId()
            );
        }

        comment = commentRepository.getCommentById(comment.getId());
        PostCommentDTO dto = buildPostCommentDto(comment, user.getId());

        messagingTemplate.convertAndSend(
                "/topic/" + post.getId() + "/postComments",
                dto
        );

        return dto;
    }

    public PaginatedResponseDTO<PostCommentDTO> getPostComments(Long postId, Long currentUserId, Integer start, Integer length) {
        int pageNumber = start / length;
        Pageable pageable = PageRequest.of(pageNumber, length, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentsPage = commentRepository.getComments(null, postId, pageable);
        List<PostCommentDTO> comments = commentsPage.getContent()
            .stream()
            .map(c -> buildPostCommentDto(c, currentUserId))
            .toList();

        return new PaginatedResponseDTO<>(
                comments,
                commentsPage.getTotalElements(),
                (int) pageable.getOffset(),
                pageable.getPageSize()
        );
    }

    public PostCommentDTO convertToPostCommentDTO(Comment comment) {
        return buildPostCommentDto(comment, null);
    }

    public PostCommentDTO buildPostCommentDto(Comment comment, Long currentUserId) {
        Friendship friendship = currentUserId != null
                ? friendshipService.findByUserAndFriendId(currentUserId, comment.getUser().getId())
                : null;

        List<StoryDTO> stories = storyService.getActiveStoryDTOsByUserId(comment.getUser().getId(), currentUserId);
        PostCommentUserDTO commentUserDTO = PostCommentUserMapper.toDto(
                comment.getUser(),
                FriendshipMapper.toDto(friendship, currentUserId),
                stories
        );

        return PostCommentMapper.toDto(comment, commentUserDTO);
    }

    private boolean isPostAuthorCommentOnOwnPost(Long authorId, Long commenterId) {
        return Objects.equals(authorId, commenterId);
    }
}
