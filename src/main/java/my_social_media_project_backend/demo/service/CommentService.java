package my_social_media_project_backend.demo.service;

import my_social_media_project_backend.demo.dto.NotificationDTO;
import my_social_media_project_backend.demo.dto.PostCommentDTO;
import my_social_media_project_backend.demo.dto.PostCommentUserDTO;
import my_social_media_project_backend.demo.dto.UserDTO;
import my_social_media_project_backend.demo.entity.Comment;
import my_social_media_project_backend.demo.entity.Notification;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.Objects;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostStatisticsService postStatisticsService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public CommentService(CommentRepository commentRepository, UserService userService, PostStatisticsService postStatisticsService, NotificationService notificationService, SimpMessagingTemplate messagingTemplate) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postStatisticsService = postStatisticsService;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    public PostCommentDTO create(Post post, User user, String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);
        commentRepository.save(comment);
        postStatisticsService.incrementCommentCount(post.getId());

        if(!isPostAuthorCommentOnOwnPost(post.getUser().getId(), user.getId())) {
            notificationService.sendNotification(
                    user,
                    post.getUser(),
                    Notification.Type.COMMENT,
                    post.getTitle(),
                    null,
                    post.getId()
            );
        }

        PostCommentDTO postCommentDTO = convertToPostCommentDTO(comment);
        messagingTemplate.convertAndSend(
                "/topic/" + post.getId() + "/postComments",
                postCommentDTO
        );

        return convertToPostCommentDTO(comment);
    }

    public Page<PostCommentDTO> getPostComments(Long postId, Integer offset, Integer recordPerPage) {
        int pageNumber = offset / recordPerPage;
        Pageable pageable = PageRequest.of(pageNumber, recordPerPage, Sort.by(Sort.Direction.DESC, "createAt"));
        return commentRepository.findPostComments(postId, pageable);
    }

    public PostCommentDTO convertToPostCommentDTO(Comment comment) {
        return new PostCommentDTO(
                comment.getId(),
                comment.getContent(),
                new PostCommentUserDTO(
                        comment.getUser().getId(),
                        comment.getUser().getUsername(),
                        comment.getUser().getUpdatedAt()
                ),
                comment.getCreateAt()
        );
    }

    private boolean isPostAuthorCommentOnOwnPost(Long authorId, Long commenterId) {
        return Objects.equals(authorId, commenterId);
    }
}
