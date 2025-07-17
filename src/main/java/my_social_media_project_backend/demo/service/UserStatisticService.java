package my_social_media_project_backend.demo.service;

import jakarta.transaction.Transactional;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.entity.UserStatistic;
import my_social_media_project_backend.demo.repository.UserStatisticRepository;
import org.springframework.stereotype.Service;

@Service
public class UserStatisticService {
    private final UserStatisticRepository userStatisticRepository;

    public UserStatisticService(UserStatisticRepository userStatisticRepository) {
        this.userStatisticRepository = userStatisticRepository;
    }

    public void create (User user) {
        UserStatistic userStatistic = new UserStatistic();
        userStatistic.setUser(user);
        userStatisticRepository.save(userStatistic);
    }

    @Transactional
    public void incrementFriendCount(Long userId) {
        userStatisticRepository.incrementFriendCount(userId);
    }

    @Transactional
    public void decrementFriendCount(Long userId) {
        userStatisticRepository.decrementFriendCount(userId);
    }

    @Transactional
    public void incrementUnseenNotificationCount(Long userId) {
        userStatisticRepository.incrementUnseenNotificationCount(userId);
    }

    @Transactional
    public void decrementUnseenNotificationCount(Long userId) {
        userStatisticRepository.decrementUnseenNotificationCount(userId);
    }

    @Transactional
    public void incrementSeenNotificationCount(Long userId) {
        userStatisticRepository.incrementSeenNotificationCount(userId);
    }

    @Transactional
    public void decrementSeenNotificationCount(Long userId) {
        userStatisticRepository.decrementSeenNotificationCount(userId);
    }

    @Transactional
    public void incrementNewNotificationCount(Long userId) {
        userStatisticRepository.incrementNewNotificationCount(userId);
    }

    @Transactional
    public void clearNewNotificationCount(Long userId) {
        userStatisticRepository.clearNewNotificationCount(userId);
    }

    @Transactional
    public void incrementPostCount(Long userId) {
        userStatisticRepository.incrementPostCount(userId);
    }

    @Transactional
    public void decrementPostCount(Long userId) {
        userStatisticRepository.decrementPostCount(userId);
    }

    @Transactional
    public void incrementLikeCount(Long userId) {
        userStatisticRepository.incrementLikeCount(userId);
    }

    @Transactional
    public void decrementLikeCount(Long userId) {
        userStatisticRepository.decrementLikeCount(userId, 1L);
    }

    @Transactional
    public void decrementLikeCount(Long userId, Long count) {
        userStatisticRepository.decrementLikeCount(userId, count);
    }
}
