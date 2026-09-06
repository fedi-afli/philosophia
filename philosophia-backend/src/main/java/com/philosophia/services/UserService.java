package com.philosophia.services;

import com.philosophia.dto.*;
import com.philosophia.enums.UserRole;
import com.philosophia.exceptions.InvalidCredentialsException;
import com.philosophia.exceptions.UserNotFoundException;
import com.philosophia.exceptions.UsernameAlreadyExistsException;
import com.philosophia.models.Student;
import com.philosophia.models.User;
import com.philosophia.repository.StudentRepository;
import com.philosophia.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    public StudentCountResponse getStudentCount() {
        return new StudentCountResponse(this.userRepository.countByRole(UserRole.STUDENT));
    }

    @Transactional
    public StudentResponse addStudent(CreateStudentRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new UsernameAlreadyExistsException(req.username());
        }

        User user = new User();
        user.setUsername(req.username());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRole(UserRole.STUDENT);
        user.setActive(true);

        Student student = new Student();
        student.setUser(user);
        student.setFirstName(req.firstName());
        student.setLastName(req.lastName());
        student.setPhone(req.phone());
        student.setInstitute(req.institute());
        // student.setSection(...); // fetch Section by req.sectionId() if provided

        userRepository.save(user);
        studentRepository.save(student);

        return new StudentResponse(student.getId(), user.getUsername(), student.getFirstName(),
                student.getLastName(), "", "", "", "", 0);
    }

    public User findById(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable : " + username));
    }
    @Transactional(readOnly = true)
    public StudentResponse getStudentProfile(User user) {
        Student student = studentRepository.findByUserIdWithSection(user.getId())
                .orElseThrow(() -> new UserNotFoundException(user.getId()));

        String sectionName = student.getSection() != null ? student.getSection().getName() : null;

        return new StudentResponse(
                student.getId(),
                user.getUsername(),
                student.getFirstName(),
                student.getLastName(),
                student.getPhone(),
                null, // no email field on Student yet — add one if you need it
                student.getInstitute(),
                sectionName,
                student.getUnpaidSessionsCount()
        );
    }


}