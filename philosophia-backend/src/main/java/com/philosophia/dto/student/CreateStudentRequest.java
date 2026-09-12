package com.philosophia.dto.student;

import com.philosophia.enums.SectionEnum;

public record CreateStudentRequest(
        String firstName,
        String lastName,
        String username,
        String password,
        String phone,
        String institute,
        SectionEnum section
) {}