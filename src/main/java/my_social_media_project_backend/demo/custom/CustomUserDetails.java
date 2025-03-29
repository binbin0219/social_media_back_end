package my_social_media_project_backend.demo.custom;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {
    private final Integer userId;

    public CustomUserDetails(Integer userId, String accountName, String password, Collection<? extends GrantedAuthority> authorities) {
        super(accountName, password, authorities);
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}
