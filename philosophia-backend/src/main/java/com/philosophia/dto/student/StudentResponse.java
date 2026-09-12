package com.philosophia.dto.student;

public record StudentResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        String phone,
        String email,
        String institute,
        String section,
        int unpaidSession

) {}