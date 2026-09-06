package com.philosophia.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Le nom d'utilisateur existe déjà : " + username);
    }
}