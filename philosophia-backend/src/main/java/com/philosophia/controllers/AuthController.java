package com.philosophia.controllers;

import com.philosophia.dto.LoginRequest;
import com.philosophia.dto.LoginResponse;
import com.philosophia.exceptions.InvalidCredentialsException;
import com.philosophia.models.RevokedToken;
import com.philosophia.repository.RevokedTokenRepository;
import com.philosophia.services.AuthService;
import com.philosophia.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final RevokedTokenRepository revokedTokenRepository;

    @Value("${jwt.cookie-secure:false}")
    private boolean cookieSecure;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse;
        try {
            loginResponse = authService.login(request);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(401).build();
        }

        ResponseCookie cookie = buildCookie(loginResponse.token(), 24 * 60 * 60);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = extractTokenFromCookie(request);
        if (token != null && jwtService.isTokenValid(token)) {
            RevokedToken revoked = new RevokedToken();
            revoked.setJti(jwtService.extractJti(token));
            revoked.setExpiresAt(jwtService.extractExpiration(token));
            revokedTokenRepository.save(revoked);
        }

        ResponseCookie expired = buildCookie("", 0);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, expired.toString())
                .build();
    }

    private ResponseCookie buildCookie(String token, int maxAgeSeconds) {
        return ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (var cookie : request.getCookies()) {
            if ("token".equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}