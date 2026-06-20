package my_social_media_project_backend.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.PostLike;
import my_social_media_project_backend.demo.entity.User;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    public Optional<PostLike> findByPostAndUser(Post post, User user);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
