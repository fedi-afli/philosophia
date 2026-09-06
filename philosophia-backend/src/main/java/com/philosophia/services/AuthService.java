package com.philosophia.services;

import com.philosophia.dto.LoginRequest;
import com.philosophia.dto.LoginResponse;
import com.philosophia.dto.StudentResponse;
import com.philosophia.enums.UserRole;
import com.philosophia.exceptions.InvalidCredentialsException;
import com.philosophia.exceptions.UserNotFoundException;
import com.philosophia.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user;
        try {
            user = userService.findByUsername(request.username());
        } catch (UserNotFoundException e) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getId(), user.getRole().name());

        StudentResponse profile = user.getRole() == UserRole.STUDENT
                ? userService.getStudentProfile(user)
                : null;

        return new LoginResponse(user.getUsername(), user.getRole().name(), token, profile);
    }
}