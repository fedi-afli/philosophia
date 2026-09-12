package com.philosophia.dto.session;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Slim, calendar-only view of a scheduled session for the current student.
 * Intentionally excludes ids, capacity, and other students' info.
 */
public record MySessionsResponse(
        Integer weekNumber,
        Integer sessionNumber,
        LocalDate sessionDate,
        LocalTime startTime,
        LocalTime endTime,
        String topic
) {}