package com.philosophia.dto.session.absence;


import java.util.List;

public record ConfirmAttendanceRequest(
        List<AttendanceEntry> attendance
) {}