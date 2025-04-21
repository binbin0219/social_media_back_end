package my_social_media_project_backend.demo.repository;

import my_social_media_project_backend.demo.dto.UserDTO;
import my_social_media_project_backend.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccountName(String accountName);

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.UserDTO(
            u.id,
            u.country,
            u.username,
            u.firstName,
            u.lastName,
            u.occupation,
            CAST(u.phoneNumber AS string),
            u.region,
            u.relationshipStatus,
            u.gender,
            u.avatar,
            u.coverUrl,
            new my_social_media_project_backend.demo.dto.FriendshipDTO(
                COALESCE(fs.userId, fs2.userId, NULL),
                COALESCE(fs.friendId, fs2.friendId, NULL),
                COALESCE(fs.status, fs2.status, NULL),
                COALESCE(fs.createAt, fs2.createAt, NULL)
            ),
            0,
            0,
            u.createAt
        )
        FROM User u
        LEFT JOIN Friendship fs ON
            (fs.user.id = u.id AND fs.friend.id = :currentUserId)
        LEFT JOIN Friendship fs2 ON
            (fs2.friend.id = u.id AND fs2.user.id = :currentUserId)
        WHERE u.id = :userId
    """)
    UserDTO getUserProfileById(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId);

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.UserDTO(
            u.id,
            u.country,
            u.username,
            u.firstName,
            u.lastName,
            u.occupation,
            CAST(u.phoneNumber AS string),
            u.region,
            u.relationshipStatus,
            u.gender,
            u.avatar,
            null,
            null,
            COALESCE(us.friendCount, 0),
            COALESCE(us.unseenNotificationCount, 0),
            u.createAt
        )
        FROM User u
        LEFT JOIN UserStatistic us ON us.userId = :userId
        WHERE u.id = :userId
    """)
    Optional<UserDTO> getCurrentUserById(@Param("userId") Long userId);
}
