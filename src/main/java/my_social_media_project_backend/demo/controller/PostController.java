package my_social_media_project_backend.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.PaginatedResponseDTO;
import my_social_media_project_backend.demo.dto.PostCreateDTO;
import my_social_media_project_backend.demo.dto.PostDTO;
import my_social_media_project_backend.demo.dto.request.SharePostRequest;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.service.PostService;
import my_social_media_project_backend.demo.service.UserService;

@RestController
@RequestMapping("api/post")
@Validated
public class PostController {
    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<PostDTO> createPost(@ModelAttribute @Valid PostCreateDTO postCreateDTO) throws BadRequestException {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getByIdOrFails(userDetails.getUserId());
        Long createdPostId = postService.create(postCreateDTO, user);
        System.out.println("createdPostId: " + createdPostId);
        PostDTO createdPost = postService.getPostDetails(createdPostId, user.getId());
        return ResponseEntity.ok().body(createdPost);
    }

    @GetMapping("/get")
    public ResponseEntity<PaginatedResponseDTO<PostDTO>> getPosts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getByIdOrFails(userDetails.getUserId());

        PaginatedResponseDTO<PostDTO> paginatedResponseDTO = postService.getPosts(pageable, user.getId());

        return ResponseEntity.ok(paginatedResponseDTO);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostDetails(
            @PathVariable Long postId
    ) {

        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        PostDTO postDTO =
                postService.getPostDetails(
                        postId,
                        userDetails.getUserId()
                );

        return ResponseEntity.ok(postDTO);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<List<PostDTO>> getPostByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "10") Integer length) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PostDTO> postDTO = postService.getPostDTOsByUserId(start, length, userId, customUserDetails.getUserId());
        return ResponseEntity.ok(postDTO);
    }

    @PostMapping("/update/{postId}")
    public ResponseEntity<Object> update(
            @PathVariable Long postId,
            @ModelAttribute @Valid PostCreateDTO postCreateDTO
    ) throws BadRequestException {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = postService.getPostByIdOrFail(postId);
        if(!Objects.equals(post.getUser().getId(), userDetails.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not able to edit this post");
        }

        User user = userService.getByIdOrFails(userDetails.getUserId());
        PostDTO updatedPost = postService.editPost(postId, postCreateDTO, user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Post updated successfully");
        response.put("updatedPost", updatedPost);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/share")
    public ResponseEntity<PostDTO> sharePost(
            @RequestBody @Valid SharePostRequest sharePostDTO
    ) throws BadRequestException {

        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user = userService.getByIdOrFails(userDetails.getUserId());

        Long sharedPostId = postService.sharePost(sharePostDTO, user);
        PostDTO sharedPost = postService.getPostDetails(sharedPostId, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(sharedPost);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> delete(
            @RequestParam Long postId
    ) {
        try {
            postService.delete(postId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Post deleted successfully");
            return ResponseEntity.ok().body(response);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not able to delete this post");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }
    }
}
