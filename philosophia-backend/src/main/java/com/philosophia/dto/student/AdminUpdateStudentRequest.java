package com.philosophia.dto.student;

import com.philosophia.enums.SectionEnum;

public record AdminUpdateStudentRequest(
        String firstName,
        String lastName,
        String phone,
        String institute,
        SectionEnum section
) {}