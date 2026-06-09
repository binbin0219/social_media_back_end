package my_social_media_project_backend.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import my_social_media_project_backend.demo.entity.PostVisibilityDeny;

@Repository
public interface PostVisibilityDenyRepository
        extends JpaRepository<PostVisibilityDeny, Long> {

        @Transactional
        @Modifying
        @Query("DELETE FROM PostVisibilityDeny pvd WHERE pvd.post.id = :postId")
        void deleteByPostId(@Param("postId") Long postId);
}