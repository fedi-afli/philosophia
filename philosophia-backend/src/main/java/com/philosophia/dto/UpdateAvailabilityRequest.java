package com.philosophia.dto;

import java.util.List;

public record UpdateAvailabilityRequest(
        List<AvailabilityRangeRequest> ranges
) {}