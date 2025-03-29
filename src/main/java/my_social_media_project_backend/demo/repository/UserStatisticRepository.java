package my_social_media_project_backend.demo.repository;

import my_social_media_project_backend.demo.entity.UserStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatisticRepository extends JpaRepository<UserStatistic, Integer> {
    @Modifying
    @Query("UPDATE UserStatistic us SET us.friendCount = us.friendCount + 1 WHERE us.userId = :userId")
    void incrementFriendCount(@Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.unseenNotificationCount = us.unseenNotificationCount + 1 WHERE us.userId = :userId")
    void incrementUnseenNotificationCount(@Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.seenNotificationCount = us.seenNotificationCount + 1 WHERE us.userId = :userId")
    void incrementSeenNotificationCount(@Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.friendCount = us.friendCount - 1 WHERE us.userId = :userId")
    void decrementFriendCount(@Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.unseenNotificationCount = us.unseenNotificationCount - 1 WHERE us.userId = :userId")
    void decrementUnseenNotificationCount(@Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE UserStatistic us SET us.seenNotificationCount = us.seenNotificationCount - 1 WHERE us.userId = :userId")
    void decrementSeenNotificationCount(@Param("userId") Integer userId);
}
