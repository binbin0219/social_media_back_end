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
import my_social_media_project_backend.demo.entity.Comment;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.enums.CommentStatus;
import my_social_media_project_backend.demo.enums.NotificationType;
import my_social_media_project_backend.demo.exception.CommentNotAllowedException;
import my_social_media_project_backend.demo.mapper.FriendshipMapper;
import my_social_media_project_backend.demo.mapper.PostCommentMapper;
import my_social_media_project_backend.demo.repository.CommentRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostStatisticsService postStatisticsService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FriendshipService friendshipService;

    public CommentService(
        CommentRepository commentRepository, 
        UserService userService, 
        PostStatisticsService postStatisticsService, 
        NotificationService notificationService, 
        SimpMessagingTemplate messagingTemplate,
        FriendshipService friendshipService
    ) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postStatisticsService = postStatisticsService;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
        this.friendshipService = friendshipService;
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
        PostCommentDTO dto = PostCommentMapper.toDto(comment, null);

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
            .map(c -> {
                Friendship friendship = friendshipService.findByUserAndFriendId(currentUserId, c.getUser().getId());
                return PostCommentMapper.toDto(c, FriendshipMapper.toDto(friendship, currentUserId));
            })
            .toList();

        return new PaginatedResponseDTO<>(
                comments,
                commentsPage.getTotalElements(),
                (int) pageable.getOffset(),
                pageable.getPageSize()
        );
    }

    public PostCommentDTO convertToPostCommentDTO(Comment comment) {
        return new PostCommentDTO(
                comment.getId(),
                comment.getContent(),
                new PostCommentUserDTO(
                        comment.getUser().getId(),
                        comment.getUser().getUsername(),
                        null,
                        comment.getUser().getUpdatedAt()
                ),
                comment.getCreateAt()
        );
    }

    private boolean isPostAuthorCommentOnOwnPost(Long authorId, Long commenterId) {
        return Objects.equals(authorId, commenterId);
    }
}
