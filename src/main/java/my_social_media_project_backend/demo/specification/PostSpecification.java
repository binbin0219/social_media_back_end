package my_social_media_project_backend.demo.specification;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CommonAbstractCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.PostVisibilityAllow;
import my_social_media_project_backend.demo.entity.PostVisibilityDeny;
import my_social_media_project_backend.demo.enums.PostPrivacySetting;

public class PostSpecification {

    public static Specification<Post> getPosts(Long userId, Long postId) {
        return Specification
            .where(filterByPostId(postId))
            .and(filterByPrivacy(userId))
            .and(filterBySharedPostPrivacy(userId));
    }

    // ─── Filter by specific postId ────────────────────────────────────────────

    private static Specification<Post> filterByPostId(Long postId) {
        return (root, query, cb) -> {
            if (postId == null) return cb.conjunction();
            return cb.equal(root.get("id"), postId);
        };
    }

    // ─── Privacy filter for the post itself ──────────────────────────────────

    private static Specification<Post> filterByPrivacy(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return cb.conjunction();

            Predicate isOwner = cb.equal(root.get("user").get("id"), userId);

            // WCV: owner OR in allow-list
            Predicate wcvAllow = buildVisibilityAllowPredicate(root, query, cb, userId, root);
            Predicate notWcv = cb.notEqual(root.get("privacySetting"), PostPrivacySetting.WCV);
            Predicate wcvOk = cb.or(notWcv, isOwner, wcvAllow);

            // WCNV: owner OR NOT in deny-list
            Predicate wcnvDeny = buildVisibilityDenyPredicate(root, query, cb, userId, root);
            Predicate notWcnv = cb.notEqual(root.get("privacySetting"), PostPrivacySetting.WCNV);
            Predicate wcnvOk = cb.or(notWcnv, isOwner, cb.not(wcnvDeny));

            // PRIVATE: owner only
            Predicate notPrivate = cb.notEqual(root.get("privacySetting"), PostPrivacySetting.PRIVATE);
            Predicate privateOk = cb.or(notPrivate, isOwner);

            // FRIENDS: owner OR accepted friendship exists
            Predicate isFriend = buildFriendshipPredicate(root, query, cb, userId, root.get("user").get("id"), "fs1");
            Predicate notFriends = cb.notEqual(root.get("privacySetting"), PostPrivacySetting.FRIENDS);
            Predicate friendsOk = cb.or(notFriends, isOwner, isFriend);

            return cb.and(wcvOk, wcnvOk, privateOk, friendsOk);
        };
    }

    // ─── Privacy filter for the shared post ──────────────────────────────────

    private static Specification<Post> filterBySharedPostPrivacy(Long userId) {
        return (root, query, cb) -> {
            Join<Post, Post> sharedPost = root.join("sharedPost", JoinType.LEFT);
            Predicate noSharedPost = cb.isNull(root.get("sharedPost"));

            if (userId == null) return cb.or(noSharedPost, cb.isNotNull(sharedPost));

            Predicate sharedIsOwner = cb.equal(sharedPost.get("user").get("id"), userId);

            // WCV
            Predicate sharedWcvAllow = buildVisibilityAllowPredicate(root, query, cb, userId, sharedPost);
            Predicate sharedNotWcv = cb.notEqual(sharedPost.get("privacySetting"), PostPrivacySetting.WCV);
            Predicate sharedWcvOk = cb.or(sharedNotWcv, sharedIsOwner, sharedWcvAllow);

            // WCNV
            Predicate sharedWcnvDeny = buildVisibilityDenyPredicate(root, query, cb, userId, sharedPost);
            Predicate sharedNotWcnv = cb.notEqual(sharedPost.get("privacySetting"), PostPrivacySetting.WCNV);
            Predicate sharedWcnvOk = cb.or(sharedNotWcnv, sharedIsOwner, cb.not(sharedWcnvDeny));

            // PRIVATE
            Predicate sharedNotPrivate = cb.notEqual(sharedPost.get("privacySetting"), PostPrivacySetting.PRIVATE);
            Predicate sharedPrivateOk = cb.or(sharedNotPrivate, sharedIsOwner);

            // FRIENDS
            Predicate sharedIsFriend = buildFriendshipPredicate(root, query, cb, userId, sharedPost.get("user").get("id"), "fs2");
            Predicate sharedNotFriends = cb.notEqual(sharedPost.get("privacySetting"), PostPrivacySetting.FRIENDS);
            Predicate sharedFriendsOk = cb.or(sharedNotFriends, sharedIsOwner, sharedIsFriend);

            Predicate sharedPostVisible = cb.and(sharedWcvOk, sharedWcnvOk, sharedPrivateOk, sharedFriendsOk);

            return cb.or(noSharedPost, sharedPostVisible);
        };
    }

    // ─── Subquery helpers ─────────────────────────────────────────────────────

    private static Predicate buildVisibilityAllowPredicate(
        Root<Post> root,
        CommonAbstractCriteria query,
        CriteriaBuilder cb,
        Long userId,
        From<?, ?> postSource
    ) {
        Subquery<Integer> sub = ((CriteriaQuery<?>) query).subquery(Integer.class);
        Root<PostVisibilityAllow> pva = sub.from(PostVisibilityAllow.class);
        sub.select(cb.literal(1))
           .where(
               cb.equal(pva.get("post"), postSource),
               cb.equal(pva.get("user").get("id"), userId)
           );
        return cb.exists(sub);
    }

    private static Predicate buildVisibilityDenyPredicate(
        Root<Post> root,
        CommonAbstractCriteria query,
        CriteriaBuilder cb,
        Long userId,
        From<?, ?> postSource
    ) {
        Subquery<Integer> sub = ((CriteriaQuery<?>) query).subquery(Integer.class);
        Root<PostVisibilityDeny> pvd = sub.from(PostVisibilityDeny.class);
        sub.select(cb.literal(1))
           .where(
               cb.equal(pvd.get("post"), postSource),
               cb.equal(pvd.get("user").get("id"), userId)
           );
        return cb.exists(sub);
    }

    private static Predicate buildFriendshipPredicate(
        Root<Post> root,
        CommonAbstractCriteria query,
        CriteriaBuilder cb,
        Long userId,
        Expression<?> targetUserId,
        String alias
    ) {
        Subquery<Integer> sub = ((CriteriaQuery<?>) query).subquery(Integer.class);
        Root<Friendship> fs = sub.from(Friendship.class);
        sub.select(cb.literal(1))
           .where(
               cb.or(
                   cb.and(
                       cb.equal(fs.get("userId"), userId),
                       cb.equal(fs.get("friendId"), targetUserId)
                   ),
                   cb.and(
                       cb.equal(fs.get("friendId"), userId),
                       cb.equal(fs.get("userId"), targetUserId)
                   )
               ),
               cb.equal(fs.get("status"), Friendship.FriendshipStatus.ACCEPTED)
           );
        return cb.exists(sub);
    }
}