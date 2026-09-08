package com.philosophia.dto;

import java.time.LocalTime;

public record UnavailabilityRangeResponse(
        Long id,
        Integer dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}