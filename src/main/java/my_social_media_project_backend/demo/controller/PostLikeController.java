package my_social_media_project_backend.demo.controller;

import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.service.PostLikeService;
import my_social_media_project_backend.demo.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/like/post")
public class PostLikeController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    public PostLikeController(PostService postService, PostLikeService postLikeService) {
        this.postService = postService;
        this.postLikeService = postLikeService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> likePost(
            @RequestBody Map<String, String> requestBody
    ) {
        Long postId = Long.parseLong(requestBody.get("post_id"));
        String action = requestBody.get("action").toLowerCase();
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Object[]> result = postService.getPostAndUserByIdOrFail(postId, userDetails.getUserId());
        Object[] row = result.get(0);
        Post post = (Post) row[0];
        User user = (User) row[1];

        switch(action) {
            case "like":
                postLikeService.likePost(post, user);
                break;

            case "unlike":
                postLikeService.unlikePost(post, user);
                break;

            default:
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid action");
                return ResponseEntity.badRequest().body(response);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Post " + action + " successfully");
        return ResponseEntity.ok().body(response);
    }
}
