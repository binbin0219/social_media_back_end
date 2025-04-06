package my_social_media_project_backend.demo.service;

import jakarta.persistence.EntityNotFoundException;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.exception.CannotAcceptFriendRequestException;
import my_social_media_project_backend.demo.exception.CannotSendFriendRequestException;
import my_social_media_project_backend.demo.exception.CannotUnsendFriendRequestException;
import my_social_media_project_backend.demo.repository.FriendshipRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

    public void sendFriendRequestByIds(Integer userId, Integer friendId)
            throws CannotSendFriendRequestException
    {
        Friendship existingFriendship = findByUserAndFriendIds(userId, friendId);
        if(existingFriendship == null) {
            createNewFriendRequest(userId, friendId);
        } else if(isRejectedByCurrentUser(existingFriendship, userId)) {
            updateFriendRequest(existingFriendship, userId, friendId);
        } else if(isRejectedByOtherUser(existingFriendship, userId)) {
            throw new CannotSendFriendRequestException("You sent friend request before but rejected by this user");
        }

        notificationService.sendNotificationByIds(
                userId,
                friendId,
                "FRIEND_REQUEST",
                null,
                null
        );
    }

    public void unsendFriendRequest(Integer userId, Integer friendId) throws CannotUnsendFriendRequestException {
        Friendship existingFriendship = findByUserAndFriendIds(userId, friendId);
        if(existingFriendship != null) {
            if(!Objects.equals(existingFriendship.getUserId(), userId)) {
                throw new CannotUnsendFriendRequestException("Only requester is able to unsend friend request");
            }
            friendshipRepository.delete(existingFriendship);
            notificationService.deleteNotification(userId, friendId, "FRIEND_REQUEST");
        }
    }

    public void acceptFriendRequestByIds(Integer userId, Integer friendId) throws CannotAcceptFriendRequestException {
        Friendship existingFriendship = findByUserAndFriendIds(userId, friendId);
        if(existingFriendship == null) {
            throw new EntityNotFoundException("Friendship not found");
        }

        if(Objects.equals(existingFriendship.getUserId(), friendId)) {
            throw new CannotAcceptFriendRequestException("Friend request cannot be accepted by the requester");
        }

        existingFriendship.setStatus(Friendship.Status.ACCEPTED);
        friendshipRepository.save(existingFriendship);
        userStatisticService.incrementFriendCount(userId);
        userStatisticService.incrementFriendCount(friendId);
        notificationService.deleteNotification(userId, friendId, "FRIEND_REQUEST");
    }

    public void rejectFriendRequestByIds(Integer userId, Integer friendId) throws CannotAcceptFriendRequestException {
        Friendship existingFriendship = findByUserAndFriendIds(userId, friendId);

        if(existingFriendship == null) {
            throw new EntityNotFoundException("Friendship not found");
        }

        if(Objects.equals(existingFriendship.getUserId(), friendId)) {
            throw new CannotAcceptFriendRequestException("Friend request cannot be rejected by the requester");
        }

        existingFriendship.setStatus(Friendship.Status.REJECTED);
        friendshipRepository.save(existingFriendship);
        notificationService.deleteNotification(userId, friendId, "FRIEND_REQUEST");
    }

    public void unfriend(Integer userId, Integer friendId) {
        friendshipRepository.deleteFriendshipBetweenUsers(userId, friendId);
        userStatisticService.decrementFriendCount(userId);
        userStatisticService.decrementFriendCount(friendId);
    }

    private Friendship findByUserAndFriendIds(Integer userId, Integer friendId) {
        return friendshipRepository.findByUserAndFriendIds(userId, friendId).orElse(null);
    }

    private void createNewFriendRequest(Integer userId, Integer friendId) {
        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);
        friendship.setStatus(Friendship.Status.PENDING);
        friendshipRepository.save(friendship);
    }

    private void updateFriendRequest(Friendship friendship, Integer userId, Integer friendId) {
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);
        friendship.setStatus(Friendship.Status.PENDING);
        friendshipRepository.save(friendship);
    }

    private boolean isRejectedByCurrentUser(Friendship friendship, Integer userId) {
        return friendship.getStatus() == Friendship.Status.REJECTED &&
                Objects.equals(friendship.getFriendId(), userId);
    }

    private boolean isRejectedByOtherUser(Friendship friendship, Integer userId) {
        return friendship.getStatus() == Friendship.Status.REJECTED &&
                Objects.equals(friendship.getUserId(), userId);
    }
}
