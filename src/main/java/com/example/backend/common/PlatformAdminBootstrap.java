package com.example.backend.common;

import com.example.backend.model.Users;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlatformAdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.password}")
    private String ADMIN_PASSWORD;

    @Value("${admin.email}")
    private String ADMIN_EMAIL;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.existsByRole("PLATFORM_ADMIN")) {
            return;
        }

        Users platformAdmin = new Users();
        platformAdmin.setName("Platform Admin");
        platformAdmin.setEmail(ADMIN_EMAIL);
        platformAdmin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        platformAdmin.setRoles(List.of("PLATFORM_ADMIN"));
        platformAdmin.setTenantId(null);

        userRepository.save(platformAdmin);
    }
}
