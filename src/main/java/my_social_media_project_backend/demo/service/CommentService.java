package my_social_media_project_backend.demo.service;

import my_social_media_project_backend.demo.dto.PostCommentDTO;
import my_social_media_project_backend.demo.dto.UserDTO;
import my_social_media_project_backend.demo.entity.Comment;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostStatisticsService postStatisticsService;

    public CommentService(CommentRepository commentRepository, UserService userService, PostStatisticsService postStatisticsService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postStatisticsService = postStatisticsService;
    }

    public PostCommentDTO create(Post post, User user, String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);
        commentRepository.save(comment);
        postStatisticsService.incrementCommentCount(post.getId());
        return convertToPostCommentDTO(comment);
    }

    public Page<Comment> getPostCommentPage(Integer postId, Integer offset, Integer recordPerPage) {
        int pageNumber = offset / recordPerPage;
        Pageable pageable = PageRequest.of(pageNumber, recordPerPage, Sort.by(Sort.Direction.DESC, "createAt"));
        return commentRepository.findLimitedByPostId(postId, pageable);
    }

    public PostCommentDTO convertToPostCommentDTO(Comment comment) {
        UserDTO userDTO = userService.mapToUserDTO(comment.getUser());
        return new PostCommentDTO(comment.getId(), comment.getContent(), userDTO, comment.getCreateAt());
    }
}
