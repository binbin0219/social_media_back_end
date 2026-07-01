package my_social_media_project_backend.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import my_social_media_project_backend.demo.dto.SearchUserDTO;
import my_social_media_project_backend.demo.dto.UserDetailsDTO;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.projection.UserSummary;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', COALESCE(:username, ''), '%'))
    """)
    Page<User> findUsers(@Param("username") String username, Pageable pageable);

    Optional<User> findByEmail(String email);

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.UserDetailsDTO(
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
                CASE
                    WHEN COALESCE(fs.userId, fs2.userId) = :currentUserId THEN true
                    ELSE false
                END,
                COALESCE(fs.createdAt, fs2.createdAt, NULL)
            ),
            null,
            null,
            null,
            null,
            null,
            u.createdAt,
            u.updatedAt
        )
        FROM User u
        LEFT JOIN Friendship fs ON
            (fs.user.id = u.id AND fs.friend.id = :currentUserId)
        LEFT JOIN Friendship fs2 ON
            (fs2.friend.id = u.id AND fs2.user.id = :currentUserId)
        WHERE u.id = :userId
    """)
    UserDetailsDTO getUserProfileById(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId);

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.UserDetailsDTO(
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
            u.createdAt,
            u.updatedAt
        )
        FROM User u
        LEFT JOIN UserStatistic us ON us.userId = :userId
        LEFT JOIN ChatRoomMember crm ON crm.user.id = :userId
        WHERE u.id = :userId
        GROUP BY u.id, u.country, u.username, u.firstName, u.lastName, u.occupation,
                 u.phoneNumber, u.region, u.relationshipStatus, u.gender,
                 us.friendCount, us.newNotificationCount, u.createdAt, us.postCount,
                 us.likeCount
    """)
    Optional<UserDetailsDTO> getCurrentUserById(@Param("userId") Long userId);


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

    @Query("""
        SELECT u
        FROM User u
        WHERE u.id != :userId
          AND NOT EXISTS (
              SELECT f.id
              FROM Friendship f
              WHERE (f.user.id = :userId AND f.friend.id = u.id)
                 OR (f.friend.id = :userId AND f.user.id = u.id)
          )
        ORDER BY FUNCTION('RANDOM')
    """)
    List<User> findRecommendedUsers(@Param("userId") Long userId, Pageable pageable);

}
