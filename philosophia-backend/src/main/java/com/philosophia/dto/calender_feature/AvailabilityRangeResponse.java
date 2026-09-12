package com.philosophia.dto.calender_feature;

import java.time.LocalTime;

public record AvailabilityRangeResponse(
        Long id,
        Integer dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}