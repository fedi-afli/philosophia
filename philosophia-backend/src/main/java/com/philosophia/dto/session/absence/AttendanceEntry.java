package com.philosophia.dto.session.absence;

public record AttendanceEntry(
        Long studentId,
        boolean absent
) {}