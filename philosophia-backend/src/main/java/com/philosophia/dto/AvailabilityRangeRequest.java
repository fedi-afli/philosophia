package com.philosophia.dto;

import java.time.LocalTime;

public record AvailabilityRangeRequest(
        Integer dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}