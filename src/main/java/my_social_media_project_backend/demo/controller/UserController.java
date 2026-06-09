package my_social_media_project_backend.demo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.activation.UnsupportedDataTypeException;
import jakarta.persistence.EntityNotFoundException;
import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.PaginatedResponseDTO;
import my_social_media_project_backend.demo.dto.PostDTO;
import my_social_media_project_backend.demo.dto.SearchUserDTO;
import my_social_media_project_backend.demo.dto.UserDTO;
import my_social_media_project_backend.demo.dto.UserDetailsDTO;
import my_social_media_project_backend.demo.dto.UserProfileUpdateDTO;
import my_social_media_project_backend.demo.dto.UserRecommendationDTO;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.exception.ValidationException;
import my_social_media_project_backend.demo.service.PostService;
import my_social_media_project_backend.demo.service.UserService;
import my_social_media_project_backend.demo.validator.UserValidator;

@RestController
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private final UserValidator userValidator;

    public UserController(UserService userService, PostService postService, UserValidator userValidator) {
        this.userService = userService;
        this.postService = postService;
        this.userValidator = userValidator;
    }

    @GetMapping
    public PaginatedResponseDTO<UserDTO> getUsers(
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int length,
            @RequestParam(required = false) String username
    ) {
        return userService.getUsers(start, length, username);
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getByIdOrNull(id);
    }

    @PostMapping("/profile/get")
    public ResponseEntity<Map<String, Object>> get(
            @RequestBody Map<String, Long> requestBody
    ) {
        Long userId = requestBody.get("userId");

        if(userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No user IDs provided"));
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsDTO user = userService.getUserProfileById(userId);
        List<PostDTO> posts = postService.getPostDTOsByUserId(0, 6, user.getId(), customUserDetails.getUserId());
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("posts", posts);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/cover/update")
    public ResponseEntity<Map<String, String>> updateCover(
            @RequestParam("image") MultipartFile file
        )
    {
        Map<String, String> response = new HashMap<>();

        try {
            String coverPublicUrl = userService.updateCover(file);
            response.put("coverPublicUrl", coverPublicUrl);
            return ResponseEntity.ok().body(response);
        } catch (UnsupportedDataTypeException e) {
            response.put("error", "Invalid file type. Only PNG and JPEG are allowed.");
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
        } catch (IOException e) {
            response.put("error", "Error saving file.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("error", "Unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/profile/update")
    public ResponseEntity<Object> updateProfile(@RequestBody UserProfileUpdateDTO dto)
    {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = new HashMap<>();

        try {
            if (dto.getAvatar() != null) userValidator.validateAvatar(dto.getAvatar());
            if (dto.getUsername() != null) userValidator.validateUsername(dto.getUsername());
            if (dto.getFirstName() != null) userValidator.validateFirstName(dto.getFirstName());
            if (dto.getLastName() != null) userValidator.validateLastName(dto.getLastName());
            if (dto.getGender() != null) userValidator.validateGender(dto.getGender());
            if (dto.getPhoneNumber() != null) userValidator.validatePhoneNumber(dto.getPhoneNumber().getFullNumber());
            if(dto.getDescription() != null) {
                dto.setDescription(userValidator.validateDes(dto.getDescription()));
            }
            if(dto.getCountry() != null) {
                dto.setCountry(userValidator.validateCountry(dto.getCountry()));
            }
            if(dto.getRegion() != null) {
                dto.setRegion(userValidator.validateRegion(dto.getRegion()));
            }
            userValidator.validateOccupation(dto.getOccupation());
            userValidator.validateRelationshipStatus(dto.getRelationshipStatus());
            userService.updateUserDetails(currentUser.getUserId(), dto);
            response.put("message", "User profile updated successfully");
            return ResponseEntity.ok().body(response);
        } catch (ValidationException | EntityNotFoundException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByUsername(
            @RequestParam("username") String username,
            @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "10") Integer length
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SearchUserDTO> SearchUserDTOS = userService.searchByUsername(username,start, length);
            response.put("searchResults", SearchUserDTOS);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            System.out.println("Failed to search user by username : " + e.getMessage());
            response.put("error", "Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/recommendations")
    public List<UserRecommendationDTO> getRecommendations(
            @RequestParam(defaultValue = "0") int limit
    ) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getRecommendedUsers(user.getUserId(), limit);
    }
}
