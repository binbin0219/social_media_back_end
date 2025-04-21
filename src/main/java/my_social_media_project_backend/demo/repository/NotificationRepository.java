package my_social_media_project_backend.demo.repository;

import my_social_media_project_backend.demo.dto.NotificationDTO;
import my_social_media_project_backend.demo.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
            SELECT new my_social_media_project_backend.demo.dto.NotificationDTO(
                n.id,
                n.recipientId,
                n.senderId,
                n.sender.avatar,
                n.sender.firstName,
                n.sender.lastName,
                n.type,
                n.content,
                n.link,
                n.seen,
                n.targetId,
                n.createAt
            )
            FROM Notification n
            WHERE n.recipientId = :recipientId
    """)
    Page<NotificationDTO> findAllByRecipientId(Pageable pageable, @Param("recipientId") Long recipientId);

    Optional<Notification> findBySenderIdAndRecipientIdAndType(Long senderId, Long recipientId, Notification.Type type);

    @Query("SELECT n FROM Notification n WHERE n.senderId = :senderId AND n.recipientId = :recipientId AND n.targetId = :targetId AND n.type = :type")
    Optional<Notification> findByTargetIdAndType(
            Long senderId,
            Long recipientId,
            Long targetId,
            Notification.Type type
    );

}
