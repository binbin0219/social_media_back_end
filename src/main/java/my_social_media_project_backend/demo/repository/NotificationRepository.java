package my_social_media_project_backend.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import my_social_media_project_backend.demo.dto.NotificationDTO;
import my_social_media_project_backend.demo.entity.Notification;
import my_social_media_project_backend.demo.enums.NotificationType;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
        SELECT n
        FROM Notification n
        WHERE
        (
            :recipientId IS NULL
            OR n.recipientId = :recipientId
        )
        AND
        (
            :senderId IS NULL
            OR n.senderId = :senderId
        )
        AND
        (
            :type IS NULL
            OR n.type = :type
        )
        ORDER BY n.createdAt DESC
    """)
    Page<Notification> getNotifications(
        @Param("recipientId") Long recipientId,
        @Param("senderId") Long senderId,
        @Param("type") NotificationType type,
        Pageable pageable
    );

    @Query("""
            SELECT new my_social_media_project_backend.demo.dto.NotificationDTO(
                n.id,
                n.recipientId,
                n.senderId,
                n.sender.username,
                n.sender.updatedAt,
                n.type,
                n.content,
                n.link,
                n.seen,
                n.targetId,
                n.createdAt
            )
            FROM Notification n
            WHERE n.recipientId = :recipientId
    """)
    Page<NotificationDTO> findAllByRecipientId(Pageable pageable, @Param("recipientId") Long recipientId);

    Optional<Notification> findBySenderIdAndRecipientIdAndType(Long senderId, Long recipientId, NotificationType type);

    @Query("SELECT n FROM Notification n WHERE n.senderId = :senderId AND n.recipientId = :recipientId AND n.targetId = :targetId AND n.type = :type")
    Optional<Notification> findByTargetIdAndType(
            Long senderId,
            Long recipientId,
            Long targetId,
            NotificationType type
    );

}
