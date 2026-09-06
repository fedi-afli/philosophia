package com.philosophia.config;

import com.philosophia.enums.UserRole;
import com.philosophia.models.User;
import com.philosophia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_USERNAME = "ftouh";
    private static final String ADMIN_PASSWORD = "99016887";

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername(ADMIN_USERNAME).isPresent()) {
            return; // déjà créé, on ne fait rien
        }

        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setPasswordHash(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole(UserRole.ADMIN);
        admin.setActive(true);

        userRepository.save(admin);
        System.out.println("Compte admin créé : " + ADMIN_USERNAME);
    }
}