package com.philosophia.dto.scheduling;

public record UnscheduledStudentResponse(
        Long studentId,
        String studentName,
        Integer weekNumber,
        String reason
) {}