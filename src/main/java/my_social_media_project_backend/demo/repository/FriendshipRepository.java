package my_social_media_project_backend.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.User;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("""
        SELECT f FROM Friendship f
        WHERE (f.userId = :userId AND f.friendId = :friendId)
        OR (f.userId = :friendId AND f.friendId = :userId)
    """)
    Optional<Friendship> findByUserAndFriendId(Long userId, Long friendId);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Friendship f
        WHERE (f.userId = :userId AND f.friendId = :friendId)
        OR (f.userId = :friendId AND f.friendId = :userId)
    """)
    void deleteFriendshipBetweenUsers(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("""
        SELECT u
        FROM User u
        JOIN Friendship f ON
            ((f.userId = :userId AND f.friendId = u.id) OR (f.friendId = :userId AND f.userId = u.id))
        WHERE (
            :status IS NULL
            OR f.status = :status
        )
        AND LOWER(u.username) LIKE LOWER(CONCAT('%', COALESCE(:username, ''), '%'))
    """)
    Page<User> getFriends(
            @Param("userId") Long userId,
            @Param("username") String username,
            @Param("status") Friendship.FriendshipStatus status,
            Pageable pageable
    );
}
