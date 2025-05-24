package my_social_media_project_backend.demo.repository;

import jakarta.transaction.Transactional;
import my_social_media_project_backend.demo.dto.FriendDTO;
import my_social_media_project_backend.demo.entity.Friendship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("""
        SELECT f FROM Friendship f
        WHERE (f.userId = :userId AND f.friendId = :friendId)
        OR (f.userId = :friendId AND f.friendId = :userId)
    """)
    Optional<Friendship> findByUserAndFriendIds(Long userId, Long friendId);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Friendship f
        WHERE (f.userId = :userId AND f.friendId = :friendId)
        OR (f.userId = :friendId AND f.friendId = :userId)
    """)
    void deleteFriendshipBetweenUsers(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.FriendDTO(
            u.id,
            u.avatar,
            u.username
        )
        FROM User u
        JOIN Friendship f ON
             ((f.userId = :userId AND f.friendId = u.id) OR (f.friendId = :userId AND f.userId = u.id))
             AND
             f.status = ACCEPTED
        WHERE u.id != :userId
    """)
    Page<FriendDTO> findFriends(@Param("userId") Long userId, Pageable pageable);

}
