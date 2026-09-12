package com.philosophia.controllers;

import com.philosophia.dto.session.MySessionsResponse;
import com.philosophia.dto.session.ScheduledSessionResponse;
import com.philosophia.dto.session.absence.ConfirmAttendanceRequest;
import com.philosophia.dto.session.absence.SessionDetailResponse;
import com.philosophia.services.SessionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionsService sessionsService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<MySessionsResponse>> getMySessions(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        List<MySessionsResponse> sessions = sessionsService.getMySessions(userId);
        System.err.println("sessions : "+   sessions);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScheduledSessionResponse>> getAllSessions() {
        return ResponseEntity.ok(sessionsService.getAllSessions());
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SessionDetailResponse> getSessionDetail(@PathVariable Long id) {
        return ResponseEntity.ok(sessionsService.getSessionDetail(id));
    }

    @PostMapping("/{id}/attendance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> confirmAttendance(
            @PathVariable Long id,
            @RequestBody ConfirmAttendanceRequest request) {
        sessionsService.confirmAttendance(id, request);
        return ResponseEntity.noContent().build();
    }
}