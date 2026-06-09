package my_social_media_project_backend.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import my_social_media_project_backend.demo.dto.PostDTO;
import my_social_media_project_backend.demo.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p , u FROM Post p , User u WHERE p.id = :postId AND u.id = :userId")
    Optional<List<Object[]>> getPostAndUserById(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("""
        SELECT 
            p,
            fs,
            CASE WHEN EXISTS (
               SELECT 1 FROM PostLike pl
               WHERE pl.post = p AND pl.user.id = :userId
            ) THEN true ELSE false END
        FROM Post p
        LEFT JOIN Friendship fs
            ON :userId IS NOT NULL 
            AND ((fs.userId = :userId AND fs.friendId = p.user.id) OR (fs.friendId = :userId AND fs.userId = p.user.id))
        WHERE
        (
            :postId IS NULL
            OR p.id = :postId
        )
        AND
        (
            :userId IS NULL
            OR p.privacySetting != WCV
            OR p.user.id = :userId
            OR EXISTS (
                SELECT 1
                FROM PostVisibilityAllow pva
                WHERE pva.post = p
                AND pva.user.id = :userId
            )
        )
        AND
        (
            :userId IS NULL
            OR p.privacySetting != WCNV
            OR p.user.id = :userId
            OR NOT EXISTS (
                SELECT 1
                FROM PostVisibilityDeny pvd
                WHERE pvd.post = p
                AND pvd.user.id = :userId
            )
        )
        AND
        (
            :userId IS NULL
            OR p.privacySetting != PRIVATE
            OR p.user.id = :userId
        )
        AND
        (
            :userId IS NULL
            OR p.privacySetting != FRIENDS
            OR p.user.id = :userId
            OR EXISTS (
                SELECT 1
                FROM Friendship fs
                WHERE ((fs.userId = :userId AND fs.friendId = p.user.id) OR (fs.friendId = :userId AND fs.userId = p.user.id))
                AND fs.status = ACCEPTED
            )
        )
    """)
    Page<Object[]> getPosts(
        @Param("userId") Long userId,
        @Param("postId") Long postId,
        Pageable pageable
    );

    default Object[] getPostById(Long userId, Long postId) {
        List<Object[]> result = getPosts(
            userId,
            postId,
            Pageable.unpaged()
        ).getContent();

        return result.isEmpty() ? null : result.get(0);
    }

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.PostDTO(
            p.id, 
            new my_social_media_project_backend.demo.dto.UserDTO(
                p.user.id,
                p.user.country,
                p.user.username,
                p.user.firstName,
                p.user.lastName,
                p.user.description,
                p.user.gender,

                null,
                null,
                null,
                null,

                p.user.updatedAt
            ),
            p.content,
            COALESCE(ps.likeCount, 0),
            COALESCE(ps.commentCount, 0),
            CASE WHEN COUNT(pl.id) > 0 THEN true ELSE false END,
            null,
            p.privacySetting,
            p.commentStatus,
            (p.isSensitiveContent = 1),
            null,
            true,
            null,
            p.createdAt
        )
        FROM Post p
        LEFT JOIN PostStatistic ps ON p.id = ps.post.id
        LEFT JOIN PostLike pl ON p.id = pl.post.id AND pl.user.id = :currentUserId
        WHERE p.user.id = :userId
        GROUP BY p.id, p.content, p.createdAt, ps.likeCount, ps.commentCount
    """)
    Page<PostDTO> getPostDTOByUserId(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId, Pageable pageable);

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.PostDTO(
            p.id,
            new my_social_media_project_backend.demo.dto.UserDTO(
                p.user.id,
                p.user.country,
                p.user.username,
                p.user.firstName,
                p.user.lastName,
                p.user.description,
                p.user.gender,
                null,
                null,
                null,
                null,
                p.user.updatedAt
            ),
            p.content,
            COALESCE(ps.likeCount, 0),
            COALESCE(ps.commentCount, 0),
            CASE WHEN COUNT(pl.id) > 0 THEN true ELSE false END,
            null,
            p.privacySetting,
            p.commentStatus,
            (p.isSensitiveContent = 1),
            null,
            true,
            null,
            p.createdAt
        )
        FROM Post p
        LEFT JOIN PostStatistic ps ON p.id = ps.post.id
        LEFT JOIN PostLike pl ON p.id = pl.post.id AND pl.user.id = :userId
        GROUP BY p.id, ps.likeCount, ps.commentCount, p.createdAt, p.user
    """)
    Page<PostDTO> getPostDTO(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    long countCommentsById(@Param("postId") Long postId);

    @Query("SELECT p.user.id FROM Post p WHERE p.id = :postId")
    Long getPostUserId(Long postId);

    Page<Post> findByUserId(Long userId, Pageable pageable);
}
