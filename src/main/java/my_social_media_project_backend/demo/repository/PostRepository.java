package my_social_media_project_backend.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import my_social_media_project_backend.demo.dto.PostDTO;
import my_social_media_project_backend.demo.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Specification<Post> spec, Pageable pageable);
    Optional<Post> findOne(Specification<Post> spec);

    @Query("SELECT p , u FROM Post p , User u WHERE p.id = :postId AND u.id = :userId")
    Optional<List<Object[]>> getPostAndUserById(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("""
        SELECT
            p,
            fs,
            CASE WHEN EXISTS (
                SELECT 1
                FROM PostLike pl
                WHERE pl.post = p
                AND pl.user.id = :userId
            ) THEN true ELSE false END
        FROM Post p
        LEFT JOIN Friendship fs
            ON :userId IS NOT NULL
            AND (
                (fs.userId = :userId AND fs.friendId = p.user.id)
                OR
                (fs.friendId = :userId AND fs.userId = p.user.id)
            )
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
                FROM Friendship fs1
                WHERE (
                    (fs1.userId = :userId AND fs1.friendId = p.user.id)
                    OR
                    (fs1.friendId = :userId AND fs1.userId = p.user.id)
                )
                AND fs1.status = ACCEPTED
            )
        )

        AND
        (
            p.sharedPost IS NULL

            OR

            (
                (
                    p.sharedPost IS NULL
                    OR :userId IS NULL
                    OR p.sharedPost.privacySetting != WCV
                    OR p.sharedPost.user.id = :userId
                    OR EXISTS (
                        SELECT 1
                        FROM PostVisibilityAllow pva2
                        WHERE pva2.post = p.sharedPost
                        AND pva2.user.id = :userId
                    )
                )

                AND

                (
                    p.sharedPost IS NULL
                    OR :userId IS NULL
                    OR p.sharedPost.privacySetting != WCNV
                    OR p.sharedPost.user.id = :userId
                    OR NOT EXISTS (
                        SELECT 1
                        FROM PostVisibilityDeny pvd2
                        WHERE pvd2.post = p.sharedPost
                        AND pvd2.user.id = :userId
                    )
                )

                AND

                (
                    p.sharedPost IS NULL
                    OR :userId IS NULL
                    OR p.sharedPost.privacySetting != PRIVATE
                    OR p.sharedPost.user.id = :userId
                )

                AND

                (
                    p.sharedPost IS NULL
                    OR :userId IS NULL
                    OR p.sharedPost.privacySetting != FRIENDS
                    OR p.sharedPost.user.id = :userId
                    OR EXISTS (
                        SELECT 1
                        FROM Friendship fs2
                        WHERE (
                            (fs2.userId = :userId AND fs2.friendId = p.sharedPost.user.id)
                            OR
                            (fs2.friendId = :userId AND fs2.userId = p.sharedPost.user.id)
                        )
                        AND fs2.status = ACCEPTED
                    )
                )
            )
        )
        """)
    Page<Object[]> getPosts(
        @Param("userId") Long userId,
        @Param("postId") Long postId,
        Pageable pageable
    );

    default Optional<Object[]> getPostById(Long userId, Long postId) {
        List<Object[]> result = getPosts(
                userId,
                postId,
                Pageable.unpaged()
        ).getContent();

        return result.isEmpty()
                ? Optional.empty()
                : Optional.of(result.get(0));
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
            COALESCE(ps.shareCount, 0),
            CASE WHEN COUNT(pl.id) > 0 THEN true ELSE false END,
            null,
            p.privacySetting,
            p.commentStatus,
            (p.isSensitiveContent = 1),
            null,
            true,
            null,
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
            COALESCE(ps.shareCount, 0),
            CASE WHEN COUNT(pl.id) > 0 THEN true ELSE false END,
            null,
            p.privacySetting,
            p.commentStatus,
            (p.isSensitiveContent = 1),
            null,
            true,
            null,
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
