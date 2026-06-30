package my_social_media_project_backend.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import my_social_media_project_backend.demo.entity.Story;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

    Page<Story> findAll(Specification<Story> spec, Pageable pageable);

    // Get all stories by a user
    List<Story> findByUserId(Long userId);

    // Get active (not expired) stories for a user
    List<Story> findByUserIdAndExpiresAtAfter(Long userId, LocalDateTime now);

    // Get all non-expired stories (useful for feed)
    List<Story> findByExpiresAtAfter(LocalDateTime now);

    // Get stories that are expired (useful for cleanup job)
    List<Story> findByExpiresAtBefore(LocalDateTime now);

    @Query("""
        SELECT s
        FROM Story s
        WHERE
        (
            :storyId IS NULL
            OR s.id = :storyId
        )

        AND
        (
            :userId IS NULL
            OR s.user.id = :userId
        )

        AND
        (
            :active IS NULL
            OR :active = false
            OR s.expiresAt IS NULL
            OR s.expiresAt > CURRENT_TIMESTAMP
        )

        AND
        (
            :currentUserId IS NULL
            OR s.user.id = :currentUserId
            OR EXISTS (
                SELECT 1 FROM Friendship fs
                WHERE (
                    (fs.userId = :currentUserId AND fs.friendId = s.user.id)
                    OR
                    (fs.friendId = :currentUserId AND fs.userId = s.user.id)
                )
                AND fs.status = ACCEPTED
            )
        )

        ORDER BY s.createdAt DESC
    """)
    Page<Story> getStories(
        @Param("userId") Long userId,
        @Param("currentUserId") Long currentUserId,
        @Param("active") Boolean active,
        @Param("storyId") Long storyId,
        Pageable pageable
    );

    default Optional<Story> getStoryById(Long storyId) {
        List<Story> result = getStories(
                null,
                null,
                true,
                storyId,
                Pageable.unpaged()
        ).getContent();

        return result.isEmpty()
                ? Optional.empty()
                : Optional.of(result.get(0));
    }

    @Query("""
        SELECT s
        FROM Story s
        WHERE s.user.id IN :userIds
        AND (s.expiresAt IS NULL OR s.expiresAt > CURRENT_TIMESTAMP)
        ORDER BY
            CASE WHEN s.user.id = :currentUserId THEN 0 ELSE 1 END ASC,
            CASE
                WHEN :currentUserId IS NOT NULL
                AND NOT EXISTS (
                    SELECT 1
                    FROM Story s2
                    WHERE s2.user.id = s.user.id
                    AND s2.user.id IN :userIds
                    AND (s2.expiresAt IS NULL OR s2.expiresAt > CURRENT_TIMESTAMP)
                    AND NOT EXISTS (
                        SELECT 1
                        FROM StoryView sv
                        WHERE sv.story.id = s2.id
                        AND sv.user.id = :currentUserId
                    )
                )
                THEN 1
                ELSE 0
            END ASC,
            s.user.id ASC,
            s.createdAt ASC
    """)
    List<Story> getActiveStoriesByUserIds(
        @Param("userIds") List<Long> userIds,
        @Param("currentUserId") Long currentUserId
    );
}
