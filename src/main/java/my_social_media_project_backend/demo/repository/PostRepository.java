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
        SELECT p
        FROM Post p
        LEFT JOIN p.sharedPost sp
        WHERE
        (
            :postId IS NULL
            OR p.id = :postId
        )
        AND
        (
            :authorId IS NULL
            OR p.user.id = :authorId
        )
        AND
        (
            :currentUserId IS NULL
            OR p.privacySetting != WCV
            OR p.user.id = :currentUserId
            OR EXISTS (
                SELECT 1 FROM PostVisibilityAllow pva
                WHERE pva.post = p AND pva.user.id = :currentUserId
            )
        )
        AND
        (
            :currentUserId IS NULL
            OR p.privacySetting != WCNV
            OR p.user.id = :currentUserId
            OR NOT EXISTS (
                SELECT 1 FROM PostVisibilityDeny pvd
                WHERE pvd.post = p AND pvd.user.id = :currentUserId
            )
        )
        AND
        (
            :currentUserId IS NULL
            OR p.privacySetting != PRIVATE
            OR p.user.id = :currentUserId
        )
        AND
        (
            :currentUserId IS NULL
            OR p.privacySetting != FRIENDS
            OR p.user.id = :currentUserId
            OR EXISTS (
                SELECT 1 FROM Friendship fs1
                WHERE (
                    (fs1.userId = :currentUserId AND fs1.friendId = p.user.id)
                    OR
                    (fs1.friendId = :currentUserId AND fs1.userId = p.user.id)
                )
                AND fs1.status = ACCEPTED
            )
        )
        AND
        (
            sp IS NULL
            OR
            (
                (
                    :currentUserId IS NULL
                    OR sp.privacySetting != WCV
                    OR sp.user.id = :currentUserId
                    OR EXISTS (
                        SELECT 1 FROM PostVisibilityAllow pva2
                        WHERE pva2.post = sp AND pva2.user.id = :currentUserId
                    )
                )
                AND
                (
                    :currentUserId IS NULL
                    OR sp.privacySetting != WCNV
                    OR sp.user.id = :currentUserId
                    OR NOT EXISTS (
                        SELECT 1 FROM PostVisibilityDeny pvd2
                        WHERE pvd2.post = sp AND pvd2.user.id = :currentUserId
                    )
                )
                AND
                (
                    :currentUserId IS NULL
                    OR sp.privacySetting != PRIVATE
                    OR sp.user.id = :currentUserId
                )
                AND
                (
                    :currentUserId IS NULL
                    OR sp.privacySetting != FRIENDS
                    OR sp.user.id = :currentUserId
                    OR EXISTS (
                        SELECT 1 FROM Friendship fs2
                        WHERE (
                            (fs2.userId = :currentUserId AND fs2.friendId = sp.user.id)
                            OR
                            (fs2.friendId = :currentUserId AND fs2.userId = sp.user.id)
                        )
                        AND fs2.status = ACCEPTED
                    )
                )
            )
        )
        """)
    Page<Post> getPosts(
        @Param("currentUserId") Long currentUserId,
        @Param("postId") Long postId,
        @Param("authorId") Long authorId,
        Pageable pageable
    );

    default Optional<Post> getPostById(Long userId, Long postId) {
        List<Post> result = getPosts(
                userId,
                postId,
                null,
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
        GROUP BY
            p.id,
            p.user.id,
            p.user.country,
            p.user.username,
            p.user.firstName,
            p.user.lastName,
            p.user.description,
            p.user.gender,
            p.user.updatedAt,
            p.content,
            ps.likeCount,
            ps.commentCount,
            ps.shareCount,
            p.privacySetting,
            p.commentStatus,
            p.isSensitiveContent,
            p.createdAt
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
