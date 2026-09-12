package com.philosophia.dto.session;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ScheduledSessionResponse(
        Long sessionId,
        Integer weekNumber,
        Integer sessionNumber,
        LocalDate sessionDate,
        LocalTime startTime,
        LocalTime endTime,
        int capacity,
        int assignedCount,
        List<String> assignedStudentNames,
        String chapterName
) {}