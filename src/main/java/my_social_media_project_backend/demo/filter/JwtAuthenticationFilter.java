package my_social_media_project_backend.demo.filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.service.UserService;
import my_social_media_project_backend.demo.utility.CookieUtils;
import my_social_media_project_backend.demo.utility.JwtUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CookieUtils cookieUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.cookieUtils = cookieUtils;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        System.out.println(path);

        if (path.startsWith("/api/test")) {
            System.out.println("Skipping filter for: " + path);
            CustomUserDetails userDetails = new CustomUserDetails(
                    1L,
                    "Anonymous",
                    "Anonymous",
                    Collections.emptyList()
            );
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
            return;
        }

        String token = cookieUtils.extractTokenFromCookie(request);
        if (token == null || !jwtUtils.isTokenValid(token)) {
            Cookie cookie = cookieUtils.createCookie("", 0);
            response.addCookie(cookie);
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtUtils.getUserIdFromToken(token);
        User userData = userService.getByIdOrNull(userId);
        if(userData == null) {
            Cookie cookie = cookieUtils.createCookie("", 0);
            response.addCookie(cookie);
            filterChain.doFilter(request, response);
            return;
        }

        CustomUserDetails userDetails = new CustomUserDetails(
                userData.getId(),
                userData.getEmail(),
                userData.getPassword(),
                Collections.emptyList()
        );
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() == null || !securityContext.getAuthentication().isAuthenticated()) {
            System.out.println("User is NOT authenticated.");
        } else {
            System.out.println("User is authenticated as: " + securityContext.getAuthentication().getName());
        }

        filterChain.doFilter(request, response);
    }
}

