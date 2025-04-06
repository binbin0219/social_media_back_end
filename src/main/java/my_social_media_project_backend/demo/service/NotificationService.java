package my_social_media_project_backend.demo.service;

import my_social_media_project_backend.demo.dto.NotificationDTO;
import my_social_media_project_backend.demo.entity.Notification;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserStatisticService userStatisticService;

    public NotificationService(NotificationRepository notificationRepository, UserStatisticService userStatisticService) {
        this.notificationRepository = notificationRepository;
        this.userStatisticService = userStatisticService;
    }

    public void sendNotificationByIds(Integer senderId, Integer recipientId, String type, String content, String link) {
        Notification notification = new Notification();
        notification.setSenderId(senderId);
        notification.setRecipientId(recipientId);
        notification.setType(type);
        notification.setContent(content);
        notification.setLink(link);
        notificationRepository.save(notification);
        userStatisticService.incrementUnseenNotificationCount(recipientId);
    }

    public void sendNotification(User sender, User recipient, String type, String content, String link) {
        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setRecipient(recipient);
        notification.setType(type);
        notification.setContent(content);
        notification.setLink(link);
        notificationRepository.save(notification);
        userStatisticService.incrementUnseenNotificationCount(recipient.getId());
    }

    public void deleteNotification(Integer senderId, Integer recipientId, String type) {
        notificationRepository.findBySenderIdAndRecipientIdAndType(senderId, recipientId, type)
            .ifPresent(notification -> {
                notificationRepository.delete(notification);
                if (notification.isSeen()) {
                    userStatisticService.decrementSeenNotificationCount(recipientId);
                } else {
                    userStatisticService.decrementUnseenNotificationCount(recipientId);
                }
            });
    }

    public List<NotificationDTO> getNotificationsByUserId(Integer recipientId, Integer offset, Integer recordPerPage) {
        int pageNumber = offset / recordPerPage;
        Pageable pageable = PageRequest.of(pageNumber, recordPerPage, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<NotificationDTO> notificationPage = notificationRepository.findAllByRecipientId(pageable, recipientId);
        return notificationPage.getContent();
    }
}
