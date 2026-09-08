package com.philosophia.exceptions;

public class SectionNotFoundException extends RuntimeException {
    public SectionNotFoundException(String name) {
        super("Section introuvable : " + name);
    }
}