package my_social_media_project_backend.demo.service;
import jakarta.persistence.EntityNotFoundException;
import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.*;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.exception.AccountNameExistedException;
import my_social_media_project_backend.demo.exception.UserNotFoundException;
import my_social_media_project_backend.demo.repository.UserRepository;
import my_social_media_project_backend.demo.utility.PasswordUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AvatarService avatarService;
    private final R2StorageService r2StorageService;

    public UserService(UserRepository userRepository, AvatarService avatarService, R2StorageService r2StorageService) {
        this.userRepository = userRepository;
        this.avatarService = avatarService;
        this.r2StorageService = r2StorageService;
    }

    public User validateUser(String accountName, String password) {
        User user = getUserByAccountName(accountName);
        if(user == null) return null;
        if(!PasswordUtil.matched(password, user.getPassword())) return null;
        return getUserByAccountName(accountName);
    }

    public User getByIdOrNull(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getByIdOrFails(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User getUserByAccountName(String accountName) {
        return userRepository.findByAccountName(accountName).orElse(null);
    }

    public UserDTO getUserProfileById(Long userId) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.getUserProfileById(userId, userDetails.getUserId());
    }

    public User signupNewUser(UserSignupDTO userSignupDTO) {
        User userWithThisAccountName = getUserByAccountName(userSignupDTO.getAccountName());
        if(userWithThisAccountName != null) throw new AccountNameExistedException("Account name already existed!");
        User newUser = new User();
        newUser.setFirstName(userSignupDTO.getFirstName());
        newUser.setLastName(userSignupDTO.getLastName());
        newUser.setUsername(userSignupDTO.getUsername());
        newUser.setAccountName(userSignupDTO.getAccountName());
        newUser.setPassword(PasswordUtil.encodePassword(userSignupDTO.getPassword()));
        newUser.setGender(userSignupDTO.getGender());
        newUser = userRepository.save(newUser);
        newUser.setAvatar(avatarService.createAvatar(newUser.getId(), newUser.getGender()));
        return userRepository.save(newUser);
    }

    public String getOrCreateUserAvatar(User user) {
        String avatarUrlFromDb = user.getAvatar();
        if(avatarUrlFromDb == null) {
            String replacedAvatarUrl = avatarService.createAvatar(user.getId(), user.getGender());
            user.setAvatar(replacedAvatarUrl);
            userRepository.save(user);
            return replacedAvatarUrl;
        }
        return avatarUrlFromDb;
    }

    public UserDTO getCurrentUserById(Long id) {
        return userRepository.getCurrentUserById(id).orElse(null);
    }

    public String updateCover(MultipartFile file)
            throws IOException,
            UnsupportedMediaTypeException
    {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found while updating cover"));
        String coverMimeType = "image/png";
        String coverPathInR2 = String.format("user/%d/cover/cover.%s", userDetails.getUserId(), coverMimeType.replace("image/", ""));

        if(file.isEmpty()) {
            throw new FileNotFoundException("Cover image is missing while updating user's cover");
        }

        String receivedType = file.getContentType();
        if(!Objects.equals(file.getContentType(), coverMimeType)) {
            throw new UnsupportedMediaTypeException(
                String.format("Expected type %s when updating user cover but received type %s", coverMimeType, receivedType)
            );
        }

        String coverPublicUrl = r2StorageService.uploadFile(coverPathInR2, file.getBytes());
        user.setCoverUrl(coverPublicUrl);
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
            String avatarUrl = avatarService.updateUserAvatar(userId, dto.getAvatar());
            long timestamp = System.currentTimeMillis();
            user.setAvatar(avatarUrl + "?v=" + timestamp);
        }
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getCountry() != null) user.setCountry(dto.getCountry());
        if (dto.getRegion() != null) user.setRegion(dto.getRegion());
        if (dto.getOccupation() != null) user.setOccupation(dto.getOccupation());
        if (dto.getRelationshipStatus() != null) user.setRelationshipStatus(dto.getRelationshipStatus());
        userRepository.save(user);
    }
}
