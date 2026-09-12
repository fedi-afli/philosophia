package com.philosophia.dto.scheduling;

import java.time.LocalDate;

public record TeachingPlanResponse(
        Long id,
        String chapterName,
        String type,
        String section,
        Integer durationWeeks,
        Integer sessionsPerWeek,
        Integer maxStudents,
        LocalDate startDate,
        String status,
        int enrolledStudents
) {}