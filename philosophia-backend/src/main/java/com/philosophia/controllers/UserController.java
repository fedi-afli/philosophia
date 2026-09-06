package com.philosophia.controllers;

import com.philosophia.dto.*;

import com.philosophia.enums.UserRole;
import com.philosophia.models.User;
import com.philosophia.repository.StudentRepository;
import com.philosophia.repository.UserRepository;
import com.philosophia.services.CredentialGeneratorService;
import com.philosophia.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CredentialGeneratorService credentialGeneratorService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final StudentRepository studentRepository;


    @PostMapping("/generate-credentials")
    public ResponseEntity<CredentialsResponse> generateCredentials(
            @RequestBody GenerateCredentialsRequest request) {
        String username = credentialGeneratorService.generateUsername(request.fullName());
        String password = credentialGeneratorService.generatePassword();
        return ResponseEntity.ok(new CredentialsResponse(username, password));
    }

    @GetMapping("/check-username")
    public ResponseEntity<CheckUsernameResponse> checkUsername(@RequestParam String username) {
        boolean available = credentialGeneratorService.isUsernameAvailable(username);
        return ResponseEntity.ok(new CheckUsernameResponse(available));
    }

    @PostMapping("/students")
    public ResponseEntity<StudentResponse> createStudent(@RequestBody CreateStudentRequest req) {
        if (!credentialGeneratorService.isUsernameAvailable(req.username())) {
            return ResponseEntity.status(409).build();
        }



        return ResponseEntity.ok(this.userService.addStudent(req));
    }


    @GetMapping("/studentCount")
    public ResponseEntity<StudentCountResponse> getStudentCount(Authentication authentication) {

        User currentUser = userService.findById(authentication);
        if (currentUser.getRole() == UserRole.STUDENT) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(this.userService.getStudentCount());
    }
}