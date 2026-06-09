package my_social_media_project_backend.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import my_social_media_project_backend.demo.UserSessionRegistry;
import my_social_media_project_backend.demo.dto.NotificationDTO;
import my_social_media_project_backend.demo.entity.Notification;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.enums.NotificationType;
import my_social_media_project_backend.demo.mapper.NotificationMapper;
import my_social_media_project_backend.demo.repository.NotificationRepository;
import my_social_media_project_backend.demo.repository.UserRepository;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserStatisticService userStatisticService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserSessionRegistry userSessionRegistry;
    private final UserRepository userRepository;

    public NotificationService(
        NotificationRepository notificationRepository, 
        UserStatisticService userStatisticService, 
        SimpMessagingTemplate messagingTemplate, 
        UserSessionRegistry userSessionRegistry,
        UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userStatisticService = userStatisticService;
        this.messagingTemplate = messagingTemplate;
        this.userSessionRegistry = userSessionRegistry;
        this.userRepository = userRepository;
    }

    public Page<NotificationDTO> getNotifications(
        Long recipientId,
            Long senderId,
            NotificationType type,
            Pageable pageable) {

        Page<Notification> notifications = notificationRepository.getNotifications(
                recipientId,
                senderId,
                type,
                pageable
        );

        return notifications.map(NotificationMapper::toDto);
    }

    public void sendNotification(
            User sender,
            User recipient,
            NotificationType type,
            String content,
            String link,
            Long targetId
    ) {
        // Prevent self-notifications
        if (sender.getId().equals(recipient.getId())) {
            return;
        }
        
        if (type == NotificationType.FRIEND_REQUEST) {
            Page<Notification> notifications = notificationRepository.getNotifications(
                    recipient.getId(),
                    sender.getId(),
                    type,
                    Pageable.unpaged()
            );

            if (!notifications.isEmpty()) {
                return; // Friend request notification already exists
            }
        }

        Notification notification = new Notification();
        notification.setSenderId(sender.getId());
        notification.setRecipientId(recipient.getId());
        notification.setType(type);
        notification.setContent(content);
        notification.setLink(link);
        notification.setTargetId(targetId);

        notificationRepository.save(notification);

        if (!userSessionRegistry.isNotificationOpen(recipient.getId())) {
            userStatisticService.incrementNewNotificationCount(recipient.getId());
        }

        messagingTemplate.convertAndSendToUser(
                String.valueOf(recipient.getId()),
                "/queue/notifications",
                new NotificationDTO(
                        notification.getId(),
                        recipient.getId(),
                        sender.getId(),
                        sender.getUsername(),
                        sender.getUpdatedAt(),
                        notification.getType(),
                        notification.getContent(),
                        notification.getLink(),
                        notification.isSeen(),
                        notification.getTargetId(),
                        notification.getCreateAt()
                )
        );
    }

    public void sendNotificationByIds(
        Long senderId,
        Long recipientId,
        NotificationType type,
        String content,
        String link,
        Long targetId
    ) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        sendNotification(sender, recipient, type, content, link, targetId);
    }

    public void deleteById(Long notificationId) {
        notificationRepository.findById(notificationId)
            .ifPresent(notification -> {
                notificationRepository.delete(notification);
            });
    }

    public void deleteByTargetIdAndType(Long senderId, Long recipientId, Long targetId, NotificationType type) {
        notificationRepository.findByTargetIdAndType(senderId, recipientId, targetId, type)
            .ifPresent(notification -> {
                notificationRepository.delete(notification);
            });
    }

    public void deleteFriendRequestNotification(Long senderId, Long recipientId) {
        notificationRepository.findBySenderIdAndRecipientIdAndType(senderId, recipientId, NotificationType.FRIEND_REQUEST)
            .ifPresent(notification -> {
                notificationRepository.delete(notification);
            });
    }

    public List<NotificationDTO> getNotificationsByUserId(Long recipientId, Integer start, Integer length) {
        int pageNumber = start / length;
        Pageable pageable = PageRequest.of(pageNumber, length, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NotificationDTO> notificationPage = notificationRepository.findAllByRecipientId(pageable, recipientId);
        return notificationPage.getContent();
    }
}
