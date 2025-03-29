package my_social_media_project_backend.demo.repository;

import my_social_media_project_backend.demo.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
