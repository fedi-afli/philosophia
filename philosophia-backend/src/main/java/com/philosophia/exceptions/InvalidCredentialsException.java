package com.philosophia.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Identifiants invalides");
    }
}