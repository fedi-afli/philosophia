package com.philosophia.security;

import com.philosophia.repository.RevokedTokenRepository;
import com.philosophia.repository.UserRepository;
import com.philosophia.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RevokedTokenRepository revokedTokenRepository;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService,
                         RevokedTokenRepository revokedTokenRepository,
                         UserRepository userRepository) {
        this.jwtService = jwtService;
        this.revokedTokenRepository = revokedTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null && jwtService.isTokenValid(token)) {
            Long userId = jwtService.extractUserId(token);
            String jti = jwtService.extractJti(token);

            boolean revoked = revokedTokenRepository.existsByJti(jti);
            boolean active = userRepository.isActive(userId).orElse(false);

            if (!revoked && active) {
                String role = jwtService.extractRole(token);

                List<GrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + role));

                var authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}