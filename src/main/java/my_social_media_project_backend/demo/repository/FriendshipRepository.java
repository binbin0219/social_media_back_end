package my_social_media_project_backend.demo.repository;

import jakarta.transaction.Transactional;
import my_social_media_project_backend.demo.entity.Friendship;
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
    Optional<Friendship> findByUserAndFriendIds(Integer userId, Integer friendId);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Friendship f
        WHERE (f.userId = :userId AND f.friendId = :friendId)
        OR (f.userId = :friendId AND f.friendId = :userId)
    """)
    void deleteFriendshipBetweenUsers(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

}
