package com.philosophia.dto.scheduling;

import com.philosophia.dto.session.ScheduledSessionResponse;

import java.util.List;

public record GenerateScheduleResponse(
        Long teachingPlanId,
        List<ScheduledSessionResponse> sessions,
        List<UnscheduledStudentResponse> unscheduledStudents
) {}