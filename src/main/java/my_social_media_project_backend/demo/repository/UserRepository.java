package my_social_media_project_backend.demo.repository;

import my_social_media_project_backend.demo.dto.SearchUserDTO;
import my_social_media_project_backend.demo.dto.UserDTO;
import my_social_media_project_backend.demo.dto.UserRecommendationDTO;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.projection.UserSummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.UserDTO(
            u.id,
            u.country,
            u.username,
            u.firstName,
            u.lastName,
            u.description,
            u.occupation,
            CAST(u.phoneNumber AS string),
            u.region,
            u.relationshipStatus,
            u.gender,
            new my_social_media_project_backend.demo.dto.FriendshipDTO(
                COALESCE(fs.userId, fs2.userId, NULL),
                COALESCE(fs.friendId, fs2.friendId, NULL),
                COALESCE(fs.status, fs2.status, NULL),
                COALESCE(fs.createAt, fs2.createAt, NULL)
            ),
            null,
            null,
            null,
            null,
            null,
            u.createAt,
            u.updatedAt
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
            u.description,
            u.occupation,
            CAST(u.phoneNumber AS string),
            u.region,
            u.relationshipStatus,
            u.gender,
            null,
            COALESCE(us.friendCount, 0),
            COALESCE(us.newNotificationCount, 0),
            COALESCE(SUM(crm.unreadCount), 0),
            COALESCE(us.postCount, 0),
            COALESCE(us.likeCount, 0),
            u.createAt,
            u.updatedAt
        )
        FROM User u
        LEFT JOIN UserStatistic us ON us.userId = :userId
        LEFT JOIN ChatRoomMember crm ON crm.user.id = :userId
        WHERE u.id = :userId
        GROUP BY u.id, u.country, u.username, u.firstName, u.lastName, u.occupation,
                 u.phoneNumber, u.region, u.relationshipStatus, u.gender,
                 us.friendCount, us.newNotificationCount, u.createAt, us.postCount,
                 us.likeCount
    """)
    Optional<UserDTO> getCurrentUserById(@Param("userId") Long userId);


    @Query("SELECT u.username AS username, u.id AS id, u.updatedAt AS updatedAt FROM User u WHERE u.id IN :ids")
    List<UserSummary> findUserSummariesByIds(@Param("ids") List<Long> ids);

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.SearchUserDTO(
            u.id,
            u.username,
            u.updatedAt
        )
        FROM User u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))
    """)
    List<SearchUserDTO> findByUsername(@Param("username") String username, Pageable pageable);

    @Query(value = """
        SELECT u.id, u.username, u.updated_at
        FROM users u
        WHERE u.id != :userId
          AND u.id NOT IN (
              SELECT CASE
                  WHEN f.user_id = :userId THEN f.friend_id
                  ELSE f.user_id
              END
              FROM friendships f
              WHERE (f.user_id = :userId OR f.friend_id = :userId)
          )
        ORDER BY RANDOM()
        LIMIT :limit;
    """, nativeQuery = true)
    List<UserRecommendationDTO> findRecommendedUsers(@Param("userId") Long userId, @Param("limit") int limit);

}
