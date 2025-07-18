package my_social_media_project_backend.demo.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.PostCreateDTO;
import my_social_media_project_backend.demo.dto.PostWithUserDTO;
import my_social_media_project_backend.demo.dto.PostWithUserIdDTO;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.service.PostService;
import my_social_media_project_backend.demo.service.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public ResponseEntity<PostWithUserIdDTO> createPost(@ModelAttribute @Valid PostCreateDTO postCreateDTO) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getByIdOrFails(userDetails.getUserId());
        PostWithUserIdDTO createdPost = postService.create(postCreateDTO, user);
        return ResponseEntity.ok().body(createdPost);
    }

    @GetMapping("/get")
    public ResponseEntity<List<PostWithUserDTO>> PostWithUserDTOs(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer recordPerPage) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PostWithUserDTO> postWithUserDTOS = postService.getPostWithUserDTOs(offset, recordPerPage, customUserDetails.getUserId());
        return ResponseEntity.ok(postWithUserDTOS);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<List<PostWithUserIdDTO>> getPostByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer recordPerPage) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PostWithUserIdDTO> postDTO = postService.getPostDTOsByUserId(offset, recordPerPage, userId, customUserDetails.getUserId());
        return ResponseEntity.ok(postDTO);
    }

    @PostMapping("/update/{postId}")
    public ResponseEntity<Object> update(
            @PathVariable Long postId,
            @RequestBody @Valid PostCreateDTO requestBody
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = postService.getPostByIdOrFail(postId);
        if(!Objects.equals(post.getUser().getId(), userDetails.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not able to edit this post");
        }

        String newTitle = requestBody.getTitle();
        String newContent = requestBody.getContent();
        postService.updatePost(post, newTitle, newContent);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Post updated successfully");
        return ResponseEntity.ok().body(response);
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
