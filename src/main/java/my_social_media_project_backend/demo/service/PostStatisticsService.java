package my_social_media_project_backend.demo.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.PostStatistic;
import my_social_media_project_backend.demo.repository.PostStatisticsRepository;

@Service
public class PostStatisticsService {

    private final PostStatisticsRepository postStatisticsRepository;

    public PostStatisticsService(PostStatisticsRepository postStatisticsRepository) {
        this.postStatisticsRepository = postStatisticsRepository;
    }

    public void create(Post post) {
        PostStatistic postStatistic = new PostStatistic(post);
        postStatisticsRepository.save(postStatistic);
    }

    public PostStatistic getByPostId(Long postId) {
        return postStatisticsRepository.findById(postId).orElse(null);
    }

    @Transactional
    public void incrementLikeCount(Long postId) {
        postStatisticsRepository.incrementLikeCount(postId);
    }

    @Transactional
    public void incrementCommentCount(Long postId) {
        postStatisticsRepository.incrementCommentCount(postId);
    }

    @Transactional
    public void incrementShareCount(Long postId) {
        postStatisticsRepository.incrementShareCount(postId);
    }

    @Transactional
    public void decrementLikeCount(Long postId) {
        postStatisticsRepository.decrementLikeCount(postId);
    }

    @Transactional
    public void decrementCommentCount(Long postId) {
        postStatisticsRepository.decrementCommentCount(postId);
    }

    @Transactional
    public void decrementShareCount(Long postId) {
        postStatisticsRepository.decrementShareCount(postId);
    }
}