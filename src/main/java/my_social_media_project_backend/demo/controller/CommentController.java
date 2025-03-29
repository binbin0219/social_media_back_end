package my_social_media_project_backend.demo.controller;

import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.PostCommentDTO;
import my_social_media_project_backend.demo.entity.Comment;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.service.CommentService;
import my_social_media_project_backend.demo.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> get(
            @RequestParam(defaultValue = "0") Integer postId,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "0") Integer recordPerPage
    ) {
        Page<Comment> postCommentPage = commentService.getPostCommentPage(postId, offset, recordPerPage);
        List<PostCommentDTO> postCommentDTOS = postCommentPage.stream().map(commentService::convertToPostCommentDTO).toList();
        boolean isAllFetched = recordPerPage > postCommentDTOS.size();
        Map<String, Object> response = new HashMap<>();
        response.put("isAllFetched", isAllFetched);
        response.put("comments", postCommentDTOS);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> commentPost(
            @RequestBody Map<String, String> requestBody
    ) {
        int postId = Integer.parseInt(requestBody.get("post_id"));
        String content = requestBody.get("content");
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Object[]> result = postService.getPostAndUserByIdOrFail(postId, userDetails.getUserId());
        Object[] row = result.getFirst();
        Post post = (Post) row[0];
        User user = (User) row[1];
        PostCommentDTO postCommentDTO = commentService.create(post, user, content);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Comment created successfully");
        response.put("comment", postCommentDTO);
        return ResponseEntity.ok().body(response);
    }
}
