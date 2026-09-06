package com.philosophia.services;

import com.philosophia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CredentialGeneratorService {

    private final UserRepository userRepository;

    private static final String PASSWORD_CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]");

    /**
     * Generates a unique username from a full name, e.g. "Amine Ben Salah" -> "amine.bensalah",
     * with a numeric suffix appended if a collision is found (amine.bensalah2, amine.bensalah3...).
     */
    public String generateUsername(String fullName) {
        String base = slugify(fullName);
        if (base.isBlank()) {
            base = "eleve";
        }
        String candidate = base;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            suffix++;
            candidate = base + suffix;
        }
        return candidate;
    }

    /** Generates a random 10-character password. Not persisted anywhere in plaintext. */
    public String generatePassword() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }

    /** Used when the user manually edits the generated username before saving. */
    public boolean isUsernameAvailable(String username) {
        return username != null
                && !username.isBlank()
                && !userRepository.existsByUsername(username);
    }

    private String slugify(String fullName) {
        String normalized = Normalizer.normalize(fullName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // strip accents
        String[] parts = normalized.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            sb.append(NON_ALNUM.matcher(p).replaceAll(""));
        }
        String joined = sb.toString();
        // fall back: firstname.lastname style if it's more readable and short enough
        if (parts.length >= 2) {
            String first = NON_ALNUM.matcher(parts[0]).replaceAll("");
            String last = NON_ALNUM.matcher(parts[parts.length - 1]).replaceAll("");
            joined = first + "." + last;
        }
        return joined;
    }
}