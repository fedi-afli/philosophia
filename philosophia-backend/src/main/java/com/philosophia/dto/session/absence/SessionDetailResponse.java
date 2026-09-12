package com.philosophia.dto.session.absence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record SessionDetailResponse(
        Long sessionId,
        String chapterName,
        LocalDate sessionDate,
        LocalTime startTime,
        LocalTime endTime,
        List<SessionStudentResponse> students
) {}