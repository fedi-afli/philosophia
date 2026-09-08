package com.philosophia.dto;

import java.util.List;

public record UpdateUnavailabilityRequest(
        List<UnavailabilityRangeRequest> ranges
) {}