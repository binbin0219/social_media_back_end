package my_social_media_project_backend.demo.controller;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.AllUnreadCountDTO;
import my_social_media_project_backend.demo.dto.UserDTO;
import my_social_media_project_backend.demo.dto.UserSignupDTO;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.service.AvatarService;
import my_social_media_project_backend.demo.service.ChatRoomService;
import my_social_media_project_backend.demo.service.UserService;
import my_social_media_project_backend.demo.utility.CookieUtils;
import my_social_media_project_backend.demo.utility.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final CookieUtils cookieUtils;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    public AuthController(CookieUtils cookieUtils, JwtUtils jwtUtils, UserService userService, ChatRoomService chatRoomService) {
        this.cookieUtils = cookieUtils;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.chatRoomService = chatRoomService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid UserSignupDTO userSignupDTO, HttpServletResponse response) {
        User newUser = userService.signupNewUser(userSignupDTO);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        User validatedUser = userService.validateUser(email, password);
        if (validatedUser != null) {
            String token = jwtUtils.createToken(validatedUser.getId());

            cookieUtils.createCookie(response, token, 60 * 60 * 24);

            return ResponseEntity.ok("Login Successful!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password!");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        cookieUtils.createCookie(response, "", 0);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(@NonNull HttpServletRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDTO userDTO = userService.getCurrentUserById(userDetails.getUserId());
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/csrf")
    public Map<String, String> getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("csrfToken", csrfToken != null ? csrfToken.getToken() : "");
        return tokenMap;
    }

    @GetMapping("/email/exist")
    public boolean checkIsEmailExisted(
        @RequestParam String email
    ) {
        try {
            User userWithProvidedEmail = userService.getUserByEmail(email);
            return userWithProvidedEmail != null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
