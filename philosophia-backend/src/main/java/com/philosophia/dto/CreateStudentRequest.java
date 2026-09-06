package com.philosophia.dto;

public record CreateStudentRequest(
        String firstName,
        String lastName,
        String username,
        String password,
        String phone,
        String institute,
        Long sectionId
) {}