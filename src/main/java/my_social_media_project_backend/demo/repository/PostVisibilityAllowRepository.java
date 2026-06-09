package my_social_media_project_backend.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import my_social_media_project_backend.demo.entity.PostVisibilityAllow;

@Repository
public interface PostVisibilityAllowRepository
        extends JpaRepository<PostVisibilityAllow, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM PostVisibilityAllow pva WHERE pva.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}