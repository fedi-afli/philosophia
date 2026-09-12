package com.philosophia.dto.session.absence;

public record SessionStudentResponse(
        Long studentId,
        String studentName,
        String attendanceStatus
) {}