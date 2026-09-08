package com.philosophia.dto;

import java.time.LocalTime;

public record AvailabilityRangeResponse(
        Long id,
        Integer dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}