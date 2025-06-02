package my_social_media_project_backend.demo.service;

import my_social_media_project_backend.demo.UserSessionRegistry;
import my_social_media_project_backend.demo.dto.NotificationDTO;
import my_social_media_project_backend.demo.entity.Notification;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserStatisticService userStatisticService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserSessionRegistry userSessionRegistry;

    public NotificationService(NotificationRepository notificationRepository, UserStatisticService userStatisticService, SimpMessagingTemplate messagingTemplate, UserSessionRegistry userSessionRegistry) {
        this.notificationRepository = notificationRepository;
        this.userStatisticService = userStatisticService;
        this.messagingTemplate = messagingTemplate;
        this.userSessionRegistry = userSessionRegistry;
    }

    public void sendNotificationByIds(Long senderId, Long recipientId, Notification.Type type, String content, String link, Long targetId) {
        Notification notification = new Notification();
        notification.setSenderId(senderId);
        notification.setRecipientId(recipientId);
        notification.setType(type);
        notification.setContent(content);
        notification.setLink(link);
        notification.setTargetId(targetId);
        notificationRepository.save(notification);
        userStatisticService.incrementNewNotificationCount(recipientId);
    }

    public void sendNotification(User sender, User recipient, Notification.Type type, String content, String link, Long targetId) {
        Notification notification = new Notification();
        notification.setSenderId(sender.getId());
        notification.setRecipientId(recipient.getId());
        notification.setType(type);
        notification.setContent(content);
        notification.setLink(link);
        notification.setTargetId(targetId);
        notificationRepository.save(notification);

        if(!userSessionRegistry.isNotificationOpen(recipient.getId())) {
            userStatisticService.incrementNewNotificationCount(recipient.getId());
        }

        messagingTemplate.convertAndSendToUser(
                String.valueOf(recipient.getId()),
                "/queue/notifications",
                new NotificationDTO(
                        notification.getId(),
                        recipient.getId(),
                        sender.getId(),
                        sender.getAvatar(),
                        sender.getUsername(),
                        notification.getType(),
                        notification.getContent(),
                        notification.getLink(),
                        notification.isSeen(),
                        notification.getTargetId(),
                        notification.getCreateAt()
                )
        );
    }

    public void deleteById(Long notificationId) {
        notificationRepository.findById(notificationId)
            .ifPresent(notification -> {
                notificationRepository.delete(notification);
            });
    }

    public void deleteByTargetIdAndType(Long senderId, Long recipientId, Long targetId, Notification.Type type) {
        notificationRepository.findByTargetIdAndType(senderId, recipientId, targetId, type)
            .ifPresent(notification -> {
                notificationRepository.delete(notification);
            });
    }

    public void deleteFriendRequestNotification(Long senderId, Long recipientId) {
        notificationRepository.findBySenderIdAndRecipientIdAndType(senderId, recipientId, Notification.Type.FRIEND_REQUEST)
                .ifPresent(notification -> {
                    notificationRepository.delete(notification);
                });
    }

    public List<NotificationDTO> getNotificationsByUserId(Long recipientId, Integer offset, Integer recordPerPage) {
        int pageNumber = offset / recordPerPage;
        Pageable pageable = PageRequest.of(pageNumber, recordPerPage, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<NotificationDTO> notificationPage = notificationRepository.findAllByRecipientId(pageable, recipientId);
        return notificationPage.getContent();
    }
}
