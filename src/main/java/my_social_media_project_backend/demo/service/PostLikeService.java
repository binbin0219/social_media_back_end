package my_social_media_project_backend.demo.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import my_social_media_project_backend.demo.dto.PostLikeDTO;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.PostLike;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.repository.PostLikeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostStatisticsService postStatisticsService;

    public PostLikeService(PostLikeRepository postLikeRepository, PostStatisticsService postStatisticsService) {
        this.postLikeRepository = postLikeRepository;
        this.postStatisticsService = postStatisticsService;
    }

    public PostLike create(Post post, User user) {
        PostLike existingPostLike = postLikeRepository.findByPostAndUser(post, user).orElse(null);
        if(existingPostLike != null) throw new EntityExistsException("User " + user.getId() + " has already liked this post (" + post.getId() + ")");
        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setUser(user);
        postStatisticsService.incrementLikeCount(post.getId());
        return postLikeRepository.save(postLike);
    }

    public void delete (Post post, User user) {
        PostLike postLike = postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(()-> new EntityNotFoundException("Post like not found"));
        postStatisticsService.decrementLikeCount(post.getId());
        postLikeRepository.delete(postLike);
    }

    public List<PostLikeDTO> convertToPostLikeDTOs(List<PostLike> postLikes) {
        return postLikes.stream()
                .map(postLike -> new PostLikeDTO(postLike.getUserId()))
                .collect(Collectors.toList());
    }
}
