package my_social_media_project_backend.demo.custom;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {
    private final Long userId;

    public CustomUserDetails(Long userId, String accountName, String password, Collection<? extends GrantedAuthority> authorities) {
        super(accountName, password, authorities);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
