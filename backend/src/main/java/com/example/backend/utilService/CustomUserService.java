package com.example.backend.utilService;

import com.example.backend.common.PagebleObject;
import com.example.backend.model.Users;
import com.example.backend.service.RestheartService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomUserService {

    private final RestheartService restheartService;
    private final PagebleObject pagebleObject;


    public UserDetailsService loadUserByUsername(String username) throws UsernameNotFoundException {
        return user -> {
            Map<String,Object> filter = Map.of("email", user);
             return restheartService.getWithFilter("users", filter)
                     .map(obj-> pagebleObject.convertValue(obj, Users.class))
                     .blockFirst();
        };
    }
}
