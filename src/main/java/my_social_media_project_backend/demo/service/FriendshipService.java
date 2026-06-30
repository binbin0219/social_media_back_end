package my_social_media_project_backend.demo.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import my_social_media_project_backend.demo.dto.FriendDTO;
import my_social_media_project_backend.demo.dto.PaginatedResponseDTO;
import my_social_media_project_backend.demo.dto.StoryDTO;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.enums.NotificationType;
import my_social_media_project_backend.demo.exception.CannotAcceptFriendRequestException;
import my_social_media_project_backend.demo.exception.CannotSendFriendRequestException;
import my_social_media_project_backend.demo.exception.CannotUnsendFriendRequestException;
import my_social_media_project_backend.demo.mapper.FriendMapper;
import my_social_media_project_backend.demo.repository.FriendshipRepository;

@Service
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final NotificationService notificationService;
    private final UserStatisticService userStatisticService;
    private final StoryService storyService;

    public FriendshipService(
            FriendshipRepository friendshipRepository,
            NotificationService notificationService,
            UserStatisticService userStatisticService,
            StoryService storyService
    ) {
        this.friendshipRepository = friendshipRepository;
        this.notificationService = notificationService;
        this.userStatisticService = userStatisticService;
        this.storyService = storyService;
    }

    public void sendFriendRequestByIds(Long userId, Long friendId)
            throws CannotSendFriendRequestException
    {
        Friendship existingFriendship = findByUserAndFriendId(userId, friendId);
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
                NotificationType.FRIEND_REQUEST,
                null,
                null,
                null
        );
    }

    public void unsendFriendRequest(Long userId, Long friendId) throws CannotUnsendFriendRequestException {
        Friendship existingFriendship = findByUserAndFriendId(userId, friendId);
        if(existingFriendship != null) {
            if(!Objects.equals(existingFriendship.getUserId(), userId)) {
                throw new CannotUnsendFriendRequestException("Only requester is able to unsend friend request");
            }
            friendshipRepository.delete(existingFriendship);
            notificationService.deleteFriendRequestNotification(userId, friendId);
        }
    }

    public void acceptFriendRequestByIds(Long userId, Long friendId) throws CannotAcceptFriendRequestException {
        Friendship existingFriendship = findByUserAndFriendId(userId, friendId);
        if(existingFriendship == null) {
            throw new EntityNotFoundException("Friendship not found");
        }

        if(Objects.equals(existingFriendship.getUserId(), friendId)) {
            throw new CannotAcceptFriendRequestException("Friend request cannot be accepted by the requester");
        }

        existingFriendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
        friendshipRepository.save(existingFriendship);
        userStatisticService.incrementFriendCount(userId);
        userStatisticService.incrementFriendCount(friendId);
        notificationService.deleteFriendRequestNotification(userId, friendId);
    }

    public void rejectFriendRequestByIds(Long userId, Long friendId) throws CannotAcceptFriendRequestException {
        Friendship existingFriendship = findByUserAndFriendId(userId, friendId);

        if(existingFriendship == null) {
            throw new EntityNotFoundException("Friendship not found");
        }

        if(Objects.equals(existingFriendship.getUserId(), friendId)) {
            throw new CannotAcceptFriendRequestException("Friend request cannot be rejected by the requester");
        }

        existingFriendship.setStatus(Friendship.FriendshipStatus.REJECTED);
        friendshipRepository.save(existingFriendship);
        notificationService.deleteFriendRequestNotification(userId, friendId);
    }

    public void unfriend(Long userId, Long friendId) {
        friendshipRepository.deleteFriendshipBetweenUsers(userId, friendId);
        userStatisticService.decrementFriendCount(userId);
        userStatisticService.decrementFriendCount(friendId);
    }

    public PaginatedResponseDTO<FriendDTO> getFriends(Long userId, String username, Friendship.FriendshipStatus status, Boolean withStories, int start, int length) {

        int page = start / length;
        Pageable pageable = PageRequest.of(page, length);

        Page<User> friendsPage =
                friendshipRepository.getFriends(userId, username, status, withStories, pageable);

        List<FriendDTO> friends = friendsPage.getContent()
            .stream()
            .map(friend -> {
                List<StoryDTO> stories = storyService.getActiveStoryDTOsByUserId(friend.getId(), userId);
                return FriendMapper.toDto(friend, stories);
            })
            .toList();

        return new PaginatedResponseDTO<>(
            friends,
            friendsPage.getTotalElements(),
            start,
            length
        );
    }

    public Boolean checkIsFriend(Long userId1, Long userId2) {
        Optional<Friendship> friendship =
                friendshipRepository.findByUserAndFriendId(userId1, userId2);

        return friendship.isPresent();
    }

    public Friendship findByUserAndFriendId(Long userId, Long friendId) {
        return friendshipRepository.findByUserAndFriendId(userId, friendId).orElse(null);
    }

    private void createNewFriendRequest(Long userId, Long friendId) {
        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);
        friendship.setStatus(Friendship.FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);
    }

    private void updateFriendRequest(Friendship friendship, Long userId, Long friendId) {
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);
        friendship.setStatus(Friendship.FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);
    }

    private boolean isRejectedByCurrentUser(Friendship friendship, Long userId) {
        return friendship.getStatus() == Friendship.FriendshipStatus.REJECTED &&
                Objects.equals(friendship.getFriendId(), userId);
    }

    private boolean isRejectedByOtherUser(Friendship friendship, Long userId) {
        return friendship.getStatus() == Friendship.FriendshipStatus.REJECTED &&
                Objects.equals(friendship.getUserId(), userId);
    }
}
