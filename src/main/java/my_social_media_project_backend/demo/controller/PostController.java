package my_social_media_project_backend.demo.controller;

import jakarta.validation.Valid;
import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.PostCreateDTO;
import my_social_media_project_backend.demo.dto.PostDTO;
import my_social_media_project_backend.demo.dto.PostWithUserDTO;
import my_social_media_project_backend.demo.dto.PostWithUserIdDTO;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.service.PostService;
import my_social_media_project_backend.demo.service.UserService;
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
    public ResponseEntity<PostWithUserIdDTO> createPost(@RequestBody @Valid PostCreateDTO postCreateDTO) {
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
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer recordPerPage) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PostWithUserIdDTO> postDTO = postService.getPostDTOsByUserId(offset, recordPerPage, userId, customUserDetails.getUserId());
        return ResponseEntity.ok(postDTO);
    }

    @PostMapping("/update")
    public ResponseEntity<Object> update(
            @RequestBody Map<String, Object> requestBody
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int postId = Integer.parseInt(requestBody.get("postId").toString());
        Post post = postService.getPostByIdOrFail(postId);
        if(!Objects.equals(post.getUser().getId(), userDetails.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not able to edit this post");
        }

        String newTitle = (String) requestBody.get("title");
        String newContent = (String) requestBody.get("content");
        if(newTitle.isEmpty() || newContent.isEmpty()) {
            return ResponseEntity.badRequest().body("Post content or title cannot be empty");
        }
        postService.updatePost(post, newTitle, newContent);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Post updated successfully");
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> delete(
            @RequestParam Integer postId
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer postUserId = postService.getPostUserId(postId);
        if(postUserId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }

        if(!Objects.equals(postUserId, userDetails.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not able to delete this post");
        }

        postService.delete(postId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Post deleted successfully");
        return ResponseEntity.ok().body(response);
    }
}
