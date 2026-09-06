package com.philosophia.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        super("Utilisateur introuvable avec l'id : " + userId);
    }
}