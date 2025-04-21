package my_social_media_project_backend.demo.service;

import jakarta.persistence.EntityNotFoundException;
import my_social_media_project_backend.demo.dto.*;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.exception.PostNotFoundException;
import my_social_media_project_backend.demo.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    private final PostStatisticsService postStatisticsService;

    public PostService(PostRepository postRepository, UserService userService, PostLikeService postLikeService, CommentService commentService, PostStatisticsService postStatisticsService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.postLikeService = postLikeService;
        this.commentService = commentService;
        this.postStatisticsService = postStatisticsService;
    }

    public PostWithUserIdDTO create(PostCreateDTO postCreateDTO, User user) {
        Post post = new Post();
        post.setUser(user);
        post.setContent(postCreateDTO.getContent());
        post.setTitle(postCreateDTO.getTitle());
        post = postRepository.save(post);
        postStatisticsService.create(post);
        return new PostWithUserIdDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                0,
                0,
                false,
                post.getCreateAt(),
                user.getId()
        );
    }

    public Post getPostByIdOrFail(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found, provided post id : " + postId));
    }

    public Long getPostUserId(Long postId) {
        return postRepository.getPostUserId(postId);
    }

    public List<Object[]> getPostAndUserByIdOrFail(Long postId, Long userId) {
        return postRepository.getPostAndUserById(postId, userId)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new EntityNotFoundException("Post or User not found"));
    }

    public List<PostWithUserIdDTO> getPostDTOsByUserId(Integer offset, Integer recordPerPage, Long userId, Long currentUserId) {
        int pageNumber = offset / recordPerPage;
        PageRequest pageable = PageRequest.of(pageNumber, recordPerPage, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<PostWithUserIdDTO> postPage = postRepository.getPostDTOByUserId(userId, currentUserId, pageable);
        return postPage.getContent();
    }

    public List<PostWithUserDTO> getPostWithUserDTOs(Integer offset, Integer recordPerPage, Long userId) {
        int pageNumber = offset / recordPerPage;
        PageRequest pageable = PageRequest.of(pageNumber, recordPerPage, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<PostWithUserDTO> postPage = postRepository.getPostWithUserDTO(userId, pageable);
        return postPage.getContent();
    }

    public List<Post> getPostsByUserId(Integer offset, Integer recordPerPage) {
        int pageNumber = offset / recordPerPage;
        PageRequest pageable = PageRequest.of(pageNumber, recordPerPage, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.getContent();
    }

    public void updatePost(Post post, String newTitle, String newContent) {
        post.setContent(newContent);
        post.setTitle(newTitle);
        postRepository.save(post);
    }

    public void delete(Long postId) {
        postRepository.deleteById(postId);
    }
}
