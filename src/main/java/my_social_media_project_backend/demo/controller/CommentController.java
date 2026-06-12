package my_social_media_project_backend.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.PaginatedResponseDTO;
import my_social_media_project_backend.demo.dto.PostCommentDTO;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.service.CommentService;
import my_social_media_project_backend.demo.service.PostService;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @GetMapping("/get")
    public ResponseEntity<PaginatedResponseDTO<PostCommentDTO>> get(
            @RequestParam(defaultValue = "0") Long postId,
            @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "0") Integer length
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PaginatedResponseDTO<PostCommentDTO> postCommentPage = commentService.getPostComments(postId, userDetails.getUserId(), start, length);
        return ResponseEntity.ok().body(postCommentPage);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> commentPost(
            @RequestBody Map<String, String> requestBody
    ) {
        Long postId = Long.parseLong(requestBody.get("post_id"));
        String content = requestBody.get("content");
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Object[]> result = postService.getPostAndUserByIdOrFail(postId, userDetails.getUserId());
        Object[] row = result.get(0);
        Post post = (Post) row[0];
        User user = (User) row[1];
        PostCommentDTO postCommentDTO = commentService.create(post, user, content);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Comment created successfully");
        response.put("comment", postCommentDTO);
        return ResponseEntity.ok().body(response);
    }
}
