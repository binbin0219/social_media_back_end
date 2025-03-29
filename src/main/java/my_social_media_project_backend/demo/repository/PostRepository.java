package my_social_media_project_backend.demo.repository;

import my_social_media_project_backend.demo.dto.PostDTO;
import my_social_media_project_backend.demo.dto.PostWithUserDTO;
import my_social_media_project_backend.demo.dto.PostWithUserIdDTO;
import my_social_media_project_backend.demo.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT p , u FROM Post p , User u WHERE p.id = :postId AND u.id = :userId")
    Optional<List<Object[]>> getPostAndUserById(@Param("postId") Integer postId, @Param("userId") Integer userId);

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.PostWithUserIdDTO(
            p.id, p.title, p.content,
            COALESCE(ps.likeCount, 0),
            COALESCE(ps.commentCount, 0),
            CASE WHEN COUNT(pl.id) > 0 THEN true ELSE false END,
            p.createAt,
            :userId
        )
        FROM Post p
        LEFT JOIN PostStatistic ps ON p.id = ps.post.id
        LEFT JOIN PostLike pl ON p.id = pl.post.id AND pl.user.id = :currentUserId
        WHERE p.user.id = :userId
        GROUP BY p.id, p.title, p.content, p.createAt, ps.likeCount, ps.commentCount
    """)
    Page<PostWithUserIdDTO> getPostDTOByUserId(@Param("userId") Integer userId, @Param("currentUserId") Integer currentUserId, Pageable pageable);

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.PostWithUserDTO(
            p.id, p.title, p.content,
            COALESCE(ps.likeCount, 0),
            COALESCE(ps.commentCount, 0),
            CASE
                WHEN COUNT(pl.id) > 0 THEN true
                ELSE false
            END,
            p.createAt,
            new my_social_media_project_backend.demo.dto.UserDTO(
                p.user.id,
                p.user.country,
                p.user.username,
                p.user.firstName,
                p.user.lastName,
                p.user.occupation,
                CAST(p.user.phoneNumber AS string),
                p.user.region,
                p.user.relationshipStatus,
                p.user.gender,
                p.user.avatar,
                null,
                null,
                0,
                0,
                p.user.createAt
            )
        )
        FROM Post p
        LEFT JOIN PostStatistic ps ON p.id = ps.post.id
        LEFT JOIN PostLike pl ON p.id = pl.post.id AND pl.user.id = :userId
        GROUP BY p.id, ps.likeCount, ps.commentCount, p.createAt, p.user
    """)
    Page<PostWithUserDTO> getPostWithUserDTO(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    long countCommentsById(@Param("postId") Integer postId);

    @Query("SELECT p.user.id FROM Post p WHERE p.id = :postId")
    Integer getPostUserId(int postId);

    Page<Post> findByUserId(Integer userId, Pageable pageable);
}
