package com.philosophia.dto.calender_feature;

import java.time.LocalTime;

public record AvailabilityRangeRequest(
        Integer dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}