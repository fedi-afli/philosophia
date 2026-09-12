package com.philosophia.dto.calender_feature;

import java.util.List;

public record UpdateAvailabilityRequest(
        List<AvailabilityRangeRequest> ranges
) {}