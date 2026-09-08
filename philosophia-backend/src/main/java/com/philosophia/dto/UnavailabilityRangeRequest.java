package com.philosophia.dto;

import java.time.LocalTime;

public record UnavailabilityRangeRequest(
        Integer dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}