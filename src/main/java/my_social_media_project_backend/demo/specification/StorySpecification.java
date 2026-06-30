package my_social_media_project_backend.demo.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.Story;

public class StorySpecification {

    public static Specification<Story> getStories(
            Long userId,              // owner filter
            Long currentUserId,       // viewer (friend filter)
            Long storyId,
            LocalDateTime referenceTime
    ) {
        return Specification
            .where(filterByStoryId(storyId))
            .and(filterByOwner(userId))
            .and(filterActive(referenceTime))
            .and(filterByFriendVisibility(currentUserId));
    }

    // ─────────────────────────────────────────────
    // 1. OWNER FILTER (stories of specific user)
    // ─────────────────────────────────────────────

    private static Specification<Story> filterByOwner(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return cb.conjunction();
            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    // ─────────────────────────────────────────────
    // 2. STORY ID FILTER
    // ─────────────────────────────────────────────

    private static Specification<Story> filterByStoryId(Long storyId) {
        return (root, query, cb) -> {
            if (storyId == null) return cb.conjunction();
            return cb.equal(root.get("id"), storyId);
        };
    }

    // ─────────────────────────────────────────────
    // 3. ACTIVE FILTER (expiresAt)
    // ─────────────────────────────────────────────

    private static Specification<Story> filterActive(LocalDateTime referenceTime) {
        return (root, query, cb) -> {

            if (referenceTime == null) {
                return cb.conjunction();
            }

            return cb.or(
                cb.isNull(root.get("expiresAt")),
                cb.greaterThan(root.get("expiresAt"), referenceTime)
            );
        };
    }

    // ─────────────────────────────────────────────
    // 4. FRIEND VISIBILITY FILTER (viewer-based)
    // ─────────────────────────────────────────────

    private static Specification<Story> filterByFriendVisibility(Long currentUserId) {
        return (root, query, cb) -> {

            if (currentUserId == null) {
                return cb.conjunction();
            }

            // owner always visible
            Expression<Long> storyOwnerId = root.get("user").get("id");

            Predicate isOwner = cb.equal(storyOwnerId, currentUserId);

            // friend check
            Subquery<Long> sub = query.subquery(Long.class);
            Root<Friendship> fs = sub.from(Friendship.class);

            sub.select(
                cb.<Long>selectCase()
                    .when(cb.equal(fs.get("userId"), currentUserId), fs.get("friendId"))
                    .otherwise(fs.get("friendId"))
            );

            sub.where(
                cb.equal(fs.get("status"), Friendship.FriendshipStatus.ACCEPTED),
                cb.or(
                    cb.equal(fs.get("userId"), currentUserId),
                    cb.equal(fs.get("friendId"), currentUserId)
                )
            );

            Predicate isFriendStory = storyOwnerId.in(sub);

            return cb.or(isOwner, isFriendStory);
        };
    }
}