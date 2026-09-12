package com.philosophia.dto.calender_feature;

import java.time.LocalTime;

public record UnavailabilityRangeResponse(
        Long id,
        Integer dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}