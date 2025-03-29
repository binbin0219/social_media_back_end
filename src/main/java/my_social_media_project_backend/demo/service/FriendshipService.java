package my_social_media_project_backend.demo.service;

import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.repository.FriendshipRepository;
import org.springframework.stereotype.Service;

@Service
public class FriendshipService {
    private final UserService userService;
    private final FriendshipRepository friendshipRepository;
    private final NotificationService notificationService;
    private final UserStatisticService userStatisticService;

    public FriendshipService(UserService userService, FriendshipRepository friendshipRepository, NotificationService notificationService, UserStatisticService userStatisticService) {
        this.userService = userService;
        this.friendshipRepository = friendshipRepository;
        this.notificationService = notificationService;
        this.userStatisticService = userStatisticService;
    }

    public void sendFriendRequestByIds(Integer userId, Integer friendId) {
        Friendship existingFriendship = findByUserAndFriendIds(userId, friendId);
        if(existingFriendship == null) {
            Friendship friendship = new Friendship();
            friendship.setUserId(userId);
            friendship.setFriendId(friendId);
            friendship.setStatus(Friendship.Status.PENDING);
            friendshipRepository.save(friendship);
        } else {
            existingFriendship.setStatus(Friendship.Status.PENDING);
            friendshipRepository.save(existingFriendship);
        }
        notificationService.sendNotificationByIds(
                userId,
                friendId,
                "FRIEND_REQUEST",
                null,
                null
        );
    }

    public Friendship findByUserAndFriendIds(Integer userId, Integer friendId) {
        return friendshipRepository.findByUserAndFriendIds(userId, friendId).orElse(null);
    }
}
