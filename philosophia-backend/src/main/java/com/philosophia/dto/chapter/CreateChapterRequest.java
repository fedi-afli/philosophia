package com.philosophia.dto.chapter;

import com.philosophia.enums.SectionEnum;
import com.philosophia.enums.SessionType;

import java.time.LocalDate;

public record CreateChapterRequest(
        String chapterName,
        SessionType type,
        SectionEnum section,
        Integer durationWeeks,
        Integer sessionsPerWeek,
        Integer maxStudents,
        LocalDate startDate
) {}