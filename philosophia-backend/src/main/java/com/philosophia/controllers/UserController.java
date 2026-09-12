package com.philosophia.controllers;

import com.philosophia.dto.authentification.CheckUsernameResponse;
import com.philosophia.dto.authentification.CredentialsResponse;
import com.philosophia.dto.authentification.GenerateCredentialsRequest;
import com.philosophia.dto.calender_feature.AvailabilityRangeResponse;
import com.philosophia.dto.calender_feature.UpdateAvailabilityRequest;
import com.philosophia.dto.calender_feature.UpdateUnavailabilityRequest;
import com.philosophia.dto.calender_feature.UnavailabilityRangeResponse;
import com.philosophia.dto.student.*;
import com.philosophia.services.CredentialGeneratorService;
import com.philosophia.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CredentialGeneratorService credentialGeneratorService;
    private final UserService userService;

    @PostMapping("/generate-credentials")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CredentialsResponse> generateCredentials(
            @RequestBody GenerateCredentialsRequest request) {
        String username = credentialGeneratorService.generateUsername(request.fullName());
        String password = credentialGeneratorService.generatePassword();
        return ResponseEntity.ok(new CredentialsResponse(username, password));
    }

    @GetMapping("/check-username")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CheckUsernameResponse> checkUsername(@RequestParam String username) {
        boolean available = credentialGeneratorService.isUsernameAvailable(username);
        return ResponseEntity.ok(new CheckUsernameResponse(available));
    }

    @PostMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponse> createStudent(@RequestBody CreateStudentRequest req) {
        if (!credentialGeneratorService.isUsernameAvailable(req.username())) {
            return ResponseEntity.status(409).build();
        }
        return ResponseEntity.ok(this.userService.addStudent(req));
    }

    @GetMapping("/studentCount")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentCountResponse> getStudentCount(Authentication authentication) {
        Long authUserId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(this.userService.getStudentCount());
    }

    @PutMapping("/students/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentResponse> updateMyProfile(
            Authentication authentication,
            @RequestBody ModifyProfileRequest request) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }
    @GetMapping("/students/me/unavailability")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<UnavailabilityRangeResponse>> getMyUnavailability(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(userService.getUnavailability(userId));
    }

    @PutMapping("/students/me/unavailability")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<UnavailabilityRangeResponse>> updateMyUnavailability(
            Authentication authentication,
            @RequestBody UpdateUnavailabilityRequest request) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(userService.updateUnavailability(userId, request));
    }
    @GetMapping("/teacher/availability")
    public ResponseEntity<List<AvailabilityRangeResponse>> getTeacherAvailability() {
        // any authenticated user can read this — students need it to see when the teacher is free
        return ResponseEntity.ok(userService.getTeacherAvailability());
    }

    @PutMapping("/teacher/availability")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AvailabilityRangeResponse>> updateTeacherAvailability(
            @RequestBody UpdateAvailabilityRequest request) {
        return ResponseEntity.ok(userService.updateTeacherAvailability(request));
    }
    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(userService.getAllStudents());
    }

    @PutMapping("/students/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponse> adminUpdateStudent(
            @PathVariable Long id,
            @RequestBody AdminUpdateStudentRequest request) {
        return ResponseEntity.ok(userService.adminUpdateStudent(id, request));
    }
}