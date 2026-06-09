package my_social_media_project_backend.demo.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import my_social_media_project_backend.demo.dto.NotificationDTO;
import my_social_media_project_backend.demo.entity.Notification;
import my_social_media_project_backend.demo.entity.User;

@Component
public class NotificationMapper {

    public NotificationMapper() {
    }

    public static NotificationDTO toDto(Notification notification) {

        User sender = notification.getSender();

        String senderUsername = sender != null ? sender.getUsername() : null;
        LocalDateTime senderUpdatedAt = sender != null ? sender.getUpdatedAt() : null;

        return new NotificationDTO(
                notification.getId(),
                notification.getRecipientId(),
                notification.getSenderId(),
                senderUsername,
                senderUpdatedAt,
                notification.getType(),
                notification.getContent(),
                notification.getLink(),
                notification.isSeen(),
                notification.getTargetId(),
                notification.getCreateAt()
        );
    }
}