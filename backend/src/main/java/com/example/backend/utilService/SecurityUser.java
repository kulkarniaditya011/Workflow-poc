package com.example.backend.utilService;

import com.example.backend.model.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {
    private final Users user;
    private final List<GrantedAuthority> authorities;

    public SecurityUser(Users user, List<String> authorities) {
        this.user = user;
        this.authorities = authorities.stream()
                .map(role-> (GrantedAuthority) new SimpleGrantedAuthority(role))
                .toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
