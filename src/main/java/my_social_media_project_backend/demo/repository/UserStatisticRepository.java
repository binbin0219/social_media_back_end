package my_social_media_project_backend.demo.repository;

import my_social_media_project_backend.demo.entity.UserStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatisticRepository extends JpaRepository<UserStatistic, Long> {
    @Modifying
    @Query("UPDATE UserStatistic us SET us.friendCount = us.friendCount + 1 WHERE us.userId = :userId")
    void incrementFriendCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.unseenNotificationCount = us.unseenNotificationCount + 1 WHERE us.userId = :userId")
    void incrementUnseenNotificationCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.seenNotificationCount = us.seenNotificationCount + 1 WHERE us.userId = :userId")
    void incrementSeenNotificationCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.newNotificationCount = us.newNotificationCount + 1 WHERE us.userId = :userId")
    void incrementNewNotificationCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.postCount = us.postCount + 1 WHERE us.userId = :userId")
    void incrementPostCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.likeCount = us.likeCount + 1 WHERE us.userId = :userId")
    void incrementLikeCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.friendCount = us.friendCount - 1 WHERE us.userId = :userId")
    void decrementFriendCount(@Param("userId") Long userId);

    @Modifying
    @Query("""
        UPDATE UserStatistic us
        SET us.unseenNotificationCount = us.unseenNotificationCount - 1
        WHERE us.userId = :userId AND us.unseenNotificationCount > 0
    """)
    void decrementUnseenNotificationCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.seenNotificationCount = us.seenNotificationCount - 1 WHERE us.userId = :userId")
    void decrementSeenNotificationCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.postCount = us.postCount - 1 WHERE us.userId = :userId")
    void decrementPostCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.likeCount = us.likeCount - :count WHERE us.userId = :userId")
    void decrementLikeCount(@Param("userId") Long userId, @Param("count") Long count);

    @Modifying
    @Query("Update UserStatistic us SET us.newNotificationCount = 0 WHERE us.userId = :userId")
    void clearNewNotificationCount(@Param("userId") Long userId);
}
