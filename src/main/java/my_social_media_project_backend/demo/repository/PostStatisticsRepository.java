package my_social_media_project_backend.demo.repository;

import my_social_media_project_backend.demo.entity.PostStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostStatisticsRepository extends JpaRepository<PostStatistic, Long> {
    @Modifying
    @Query("UPDATE PostStatistic ps SET ps.likeCount = ps.likeCount + 1 WHERE ps.post.id = :postId")
    void incrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE PostStatistic ps SET ps.commentCount = ps.commentCount + 1 WHERE ps.post.id = :postId")
    void incrementCommentCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE PostStatistic ps SET ps.likeCount = ps.likeCount - 1 WHERE ps.post.id = :postId AND ps.likeCount > 0")
    void decrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE PostStatistic ps SET ps.commentCount = ps.commentCount - 1 WHERE ps.post.id = :postId AND ps.commentCount > 0")
    void decrementCommentCount(@Param("postId") Long postId);
}
