package my_social_media_project_backend.demo.service;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;

import jakarta.persistence.EntityNotFoundException;
import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.PaginatedResponseDTO;
import my_social_media_project_backend.demo.dto.SearchUserDTO;
import my_social_media_project_backend.demo.dto.StoryDTO;
import my_social_media_project_backend.demo.dto.UserDTO;
import my_social_media_project_backend.demo.dto.UserDetailsDTO;
import my_social_media_project_backend.demo.dto.UserProfileUpdateDTO;
import my_social_media_project_backend.demo.dto.UserRecommendationDTO;
import my_social_media_project_backend.demo.dto.UserSignupDTO;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.exception.UserNotFoundException;
import my_social_media_project_backend.demo.exception.emailExistedException;
import my_social_media_project_backend.demo.mapper.FriendshipMapper;
import my_social_media_project_backend.demo.mapper.UserMapper;
import my_social_media_project_backend.demo.repository.UserRepository;
import my_social_media_project_backend.demo.utility.PasswordUtil;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AvatarService avatarService;
    private final R2StorageService r2StorageService;
    private final UserStatisticService userStatisticService;
    private final FriendshipService friendshipService;
    private final StoryService storyService;

    public UserService(
            UserRepository userRepository,
            AvatarService avatarService,
            R2StorageService r2StorageService,
            UserStatisticService userStatisticService,
            FriendshipService friendshipService,
            StoryService storyService
    ) {
        this.userRepository = userRepository;
        this.avatarService = avatarService;
        this.r2StorageService = r2StorageService;
        this.userStatisticService = userStatisticService;
        this.friendshipService = friendshipService;
        this.storyService = storyService;
    }

    public PaginatedResponseDTO<UserDTO> getUsers(int start, int length, String username) {

        int page = start / length; // convert offset → page number

        Pageable pageable = PageRequest.of(page, length, Sort.by("id").descending());
        Page<User> usersPage = userRepository.findUsers(username, pageable);
        Long currentUserId = getCurrentUserId();

        List<UserDTO> users = usersPage.getContent()
            .stream()
            .map(user -> buildUserDto(user, currentUserId))
            .toList();

        return new PaginatedResponseDTO<>(
            users,
            usersPage.getTotalElements(),
            start,
            length
        );
    }

    public User validateUser(String email, String password) {
        User user = getUserByEmail(email);
        if(user == null) return null;
        if(!PasswordUtil.matched(password, user.getPassword())) return null;
        return getUserByEmail(email);
    }

    public User getByIdOrNull(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getByIdOrFails(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public UserDetailsDTO getUserProfileById(Long userId) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.getUserProfileById(userId, userDetails.getUserId());
    }

    public User signupNewUser(UserSignupDTO userSignupDTO) {
        User userWithThisEmail = getUserByEmail(userSignupDTO.getEmail());
        if(userWithThisEmail != null) throw new emailExistedException("Account name already existed!");
        User newUser = new User();
        newUser.setUsername(userSignupDTO.getUsername());
        newUser.setEmail(userSignupDTO.getEmail());
        newUser.setPassword(PasswordUtil.encodePassword(userSignupDTO.getPassword()));
        newUser.setGender(userSignupDTO.getGender());
        newUser = userRepository.save(newUser);
        userStatisticService.create(newUser);

        // Try to create avatar but ignore failure
        try {
            avatarService.createAvatar(newUser.getId(), newUser.getGender());
        } catch (Exception e) {
            System.err.println("Failed to create avatar for user " + newUser.getId() + ": " + e.getMessage());
        }

        return newUser;
    }

    public UserDetailsDTO getCurrentUserById(Long id) {
        return userRepository.getCurrentUserById(id).orElse(null);
    }

    public String updateCover(MultipartFile file)
            throws IOException,
            UnsupportedMediaTypeException
    {
        if(file.isEmpty() || file.getContentType() == null) {
            throw new FileNotFoundException("Cover image or content type is missing while updating user's cover");
        }

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found while updating cover"));
        String receivedType = file.getContentType();
        String coverPathInR2 = String.format("user/%d/cover/cover.%s", userDetails.getUserId(), receivedType.replace("image/", ""));

        if(!receivedType.startsWith("image/")) {
            throw new UnsupportedMediaTypeException(
                String.format("Expected type image when updating user cover but received type %s", receivedType)
            );
        }

        String coverPublicUrl = r2StorageService.uploadFile(coverPathInR2, file.getBytes());
        userRepository.save(user);
        return coverPublicUrl;
    }

    public void updateUserDetails(Long userId, UserProfileUpdateDTO dto)
        throws EntityNotFoundException
    {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new EntityNotFoundException("Failed to update user profile: user not found");
        }

        if (dto.getAvatar() != null) {
            avatarService.updateUserAvatar(userId, dto.getAvatar());
        }
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getDescription() != null) user.setDescription(dto.getDescription());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getCountry() != null) user.setCountry(dto.getCountry());
        if (dto.getRegion() != null) user.setRegion(dto.getRegion());
        if (dto.getOccupation() != null) user.setOccupation(dto.getOccupation());
        if (dto.getRelationshipStatus() != null) user.setRelationshipStatus(dto.getRelationshipStatus());
        userRepository.save(user);
    }

    public List<SearchUserDTO> searchByUsername(String username, Integer start, Integer length) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be blank!");
        }

        int pageNumber = start / length;
        PageRequest pageable = PageRequest.of(pageNumber, length, Sort.by(Sort.Direction.ASC, "username"));
        return userRepository.findByUsername(username, pageable);
    }

    public List<UserRecommendationDTO> getRecommendedUsers(Long currentUserId, int limit) {
        return userRepository.findRecommendedUsers(currentUserId, PageRequest.of(0, limit))
            .stream()
            .map(user -> buildUserRecommendationDto(user, currentUserId))
            .toList();
    }

    public UserRecommendationDTO buildUserRecommendationDto(User user, Long currentUserId) {
        List<StoryDTO> storyDTOs = storyService.getActiveStoryDTOsByUserId(user.getId(), currentUserId);
        Friendship friendship = friendshipService.findByUserAndFriendId(user.getId(), currentUserId);

        return new UserRecommendationDTO(
            user.getId(),
            user.getUsername(),
            FriendshipMapper.toDto(friendship, currentUserId),
            storyDTOs,
            user.getUpdatedAt()
        );
    }

    public UserDTO buildUserDto(User user, Long currentUserId) {
        List<StoryDTO> storyDTOs = storyService.getActiveStoryDTOsByUserId(user.getId(), currentUserId);

        Friendship friendship = friendshipService.findByUserAndFriendId(user.getId(), currentUserId);

        return UserMapper.toDto(user, friendship, storyDTOs);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return null;
        }

        return userDetails.getUserId();
    }
}
