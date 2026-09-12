package com.philosophia.dto.calender_feature;

import java.util.List;

public record UpdateUnavailabilityRequest(
        List<UnavailabilityRangeRequest> ranges
) {}