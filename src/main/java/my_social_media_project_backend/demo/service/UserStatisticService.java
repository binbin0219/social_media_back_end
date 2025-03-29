package my_social_media_project_backend.demo.service;

import jakarta.transaction.Transactional;
import my_social_media_project_backend.demo.repository.UserStatisticRepository;
import org.springframework.stereotype.Service;

@Service
public class UserStatisticService {
    private final UserStatisticRepository userStatisticRepository;

    public UserStatisticService(UserStatisticRepository userStatisticRepository) {
        this.userStatisticRepository = userStatisticRepository;
    }

    @Transactional
    public void incrementFriendCount(Integer userId) {
        userStatisticRepository.incrementFriendCount(userId);
    }

    @Transactional
    public void decrementFriendCount(Integer userId) {
        userStatisticRepository.decrementFriendCount(userId);
    }

    @Transactional
    public void incrementUnseenNotificationCount(Integer userId) {
        userStatisticRepository.incrementUnseenNotificationCount(userId);
    }

    @Transactional
    public void decrementUnseenNotificationCount(Integer userId) {
        userStatisticRepository.decrementUnseenNotificationCount(userId);
    }

    @Transactional
    public void incrementSeenNotificationCount(Integer userId) {
        userStatisticRepository.incrementSeenNotificationCount(userId);
    }

    @Transactional
    public void decrementSeenNotificationCount(Integer userId) {
        userStatisticRepository.decrementSeenNotificationCount(userId);
    }
}
