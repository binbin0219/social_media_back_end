package my_social_media_project_backend.demo.repository;

import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.PostLike;
import my_social_media_project_backend.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    public Optional<PostLike> findByPostAndUser(Post post, User user);
}
