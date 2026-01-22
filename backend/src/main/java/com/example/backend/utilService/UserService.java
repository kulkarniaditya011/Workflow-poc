package com.example.backend.utilService;

import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.Role;
import com.example.backend.model.Users;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    public UserDetailsService userDetailsService(){

        return username -> {
            Users user = userRepository.findByEmail(username)
                    .orElseThrow(() ->  {log.error("User not found in DB for email: {}", username);
                           return new UsernameNotFoundException("User not found");}
                    );
            log.info("User loaded from DB: {}", user.toString());

            // Fetch roles
            List<Role> roles = roleRepository.findByNameIn(user.getRoles());
            log.error("error: {}",roleRepository.findByNameIn(user.getRoles()).toString());
            log.info("Roles loaded from DB: {}", roles.toString());
            if (roles.isEmpty()) {
                throw new RestApiException("Role not found", HttpStatus.BAD_REQUEST);
            }

            // Merge authorities from all roles
            Set<String> authorities =
                    roles.stream()
                            .flatMap(role -> {
                                Set<String> auths = new HashSet<>();
                                auths.add("ROLE_" + role.getName());
                                auths.addAll(role.getAuthorities());
                                return auths.stream();
                            })
                            .collect(Collectors.toSet());

            return new SecurityUser(user, new ArrayList<>(authorities));
        };
    }

}

