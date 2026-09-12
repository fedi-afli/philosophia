package com.philosophia.controllers;

import com.philosophia.dto.chapter.CreateChapterRequest;
import com.philosophia.dto.scheduling.GenerateScheduleResponse;
import com.philosophia.dto.scheduling.TeachingPlanResponse;
import com.philosophia.dto.chapter.UpdateChapterRequest;
import com.philosophia.schedulingEngine.SchedulingService;
import com.philosophia.services.TeachingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final TeachingPlanService teachingPlanService;
    private final SchedulingService schedulingService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeachingPlanResponse> createChapter(@RequestBody CreateChapterRequest request) {
        return ResponseEntity.ok(teachingPlanService.createChapter(request));
    }

    @GetMapping
    public ResponseEntity<List<TeachingPlanResponse>> getAllChapters() {
        return ResponseEntity.ok(teachingPlanService.getAllTeachingPlans());
    }
    // add to ChapterController
    @PostMapping("/{id}/schedule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenerateScheduleResponse> generateSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(schedulingService.generateSchedule(id));
    }
    @GetMapping("/{id}")
    public ResponseEntity<TeachingPlanResponse> getChapterById(@PathVariable Long id) {
        return ResponseEntity.ok(teachingPlanService.getChapterById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeachingPlanResponse> updateChapter(
            @PathVariable Long id,
            @RequestBody UpdateChapterRequest request) {
        return ResponseEntity.ok(teachingPlanService.updateChapter(id, request));
    }
}