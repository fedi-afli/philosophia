package com.philosophia.services;

import com.philosophia.dto.*;
import com.philosophia.enums.SectionEnum;
import com.philosophia.enums.UserRole;

import com.philosophia.exceptions.InvalidUnavailabilityException;
import com.philosophia.exceptions.SectionNotFoundException;
import com.philosophia.exceptions.UserNotFoundException;
import com.philosophia.exceptions.UsernameAlreadyExistsException;
import com.philosophia.models.Student;
import com.philosophia.models.StudentUnavailability;
import com.philosophia.models.TeacherAvailability;
import com.philosophia.models.User;
import com.philosophia.repository.StudentRepository;
import com.philosophia.repository.TeacherAvailabilityRepository;
import com.philosophia.repository.UserRepository;

import com.philosophia.repository.UnavailabilityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final UnavailabilityRepository unavailabilityRepository;
    private final TeacherAvailabilityRepository teacherAvailabilityRepository;

    public UserService(TeacherAvailabilityRepository teacherAvailabilityRepository, UserRepository userRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder, UnavailabilityRepository unavailabilityRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.unavailabilityRepository = unavailabilityRepository;
        this.teacherAvailabilityRepository=teacherAvailabilityRepository;
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
        // student.setSection(...); // set from req if CreateStudentRequest carries a section value

        userRepository.save(user);
        studentRepository.save(student);

        return new StudentResponse(student.getId(), user.getUsername(), student.getFirstName(),
                student.getLastName(), "", "", "", "", 0);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable : " + username));
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentProfile(User user) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UserNotFoundException(user.getId()));

        String sectionName = student.getSection() != null ? student.getSection().name() : null;

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

    @Transactional
    public StudentResponse updateProfile(Long userId, ModifyProfileRequest req) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (req.phone() != null && !req.phone().isBlank()) {
            student.setPhone(req.phone());
        }
        if (req.institute() != null && !req.institute().isBlank()) {
            student.setInstitute(req.institute());
        }
        if (req.section() != null && !req.section().isBlank()) {
            try {
                student.setSection(SectionEnum.valueOf(req.section().toUpperCase()));

            } catch (IllegalArgumentException e) {
                throw new SectionNotFoundException(req.section());
            }
        }
        System.out.println("section :"+req.section());

        studentRepository.save(student);

        return getStudentProfile(student.getUser());
    }
    @Transactional
    public List<UnavailabilityRangeResponse> updateUnavailability(Long userId, UpdateUnavailabilityRequest req) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        for (UnavailabilityRangeRequest range : req.ranges()) {
            if (range.dayOfWeek() == null || range.dayOfWeek() < 0 || range.dayOfWeek() > 6) {
                throw new InvalidUnavailabilityException("Jour invalide : " + range.dayOfWeek());
            }
            if (range.startTime() == null || range.endTime() == null || !range.startTime().isBefore(range.endTime())) {
                throw new InvalidUnavailabilityException("Plage horaire invalide pour le jour " + range.dayOfWeek());
            }
        }

        unavailabilityRepository.deleteByStudentId(student.getId());

        List<StudentUnavailability> entities = req.ranges().stream()
                .map(range -> {
                    StudentUnavailability u = new StudentUnavailability();
                    u.setStudent(student);
                    u.setDayOfWeek(range.dayOfWeek());
                    u.setStartTime(range.startTime());
                    u.setEndTime(range.endTime());
                    return u;
                })
                .toList();

        unavailabilityRepository.saveAll(entities);

        return entities.stream()
                .map(u -> new UnavailabilityRangeResponse(u.getId(), u.getDayOfWeek(), u.getStartTime(), u.getEndTime()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UnavailabilityRangeResponse> getUnavailability(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return unavailabilityRepository.findByStudentId(student.getId()).stream()
                .map(u -> new UnavailabilityRangeResponse(u.getId(), u.getDayOfWeek(), u.getStartTime(), u.getEndTime()))
                .toList();
    }




    @Transactional
    public List<AvailabilityRangeResponse> updateTeacherAvailability(UpdateAvailabilityRequest req) {
        for (AvailabilityRangeRequest range : req.ranges()) {
            if (range.dayOfWeek() == null || range.dayOfWeek() < 0 || range.dayOfWeek() > 6) {
                throw new InvalidUnavailabilityException("Jour invalide : " + range.dayOfWeek());
            }
            if (range.startTime() == null || range.endTime() == null || !range.startTime().isBefore(range.endTime())) {
                throw new InvalidUnavailabilityException("Plage horaire invalide pour le jour " + range.dayOfWeek());
            }
        }

        teacherAvailabilityRepository.deleteAll();

        List<TeacherAvailability> entities = req.ranges().stream()
                .map(range -> {
                    TeacherAvailability a = new TeacherAvailability();
                    a.setDayOfWeek(range.dayOfWeek());
                    a.setStartTime(range.startTime());
                    a.setEndTime(range.endTime());
                    return a;
                })
                .toList();

        teacherAvailabilityRepository.saveAll(entities);

        return entities.stream()
                .map(a -> new AvailabilityRangeResponse(a.getId(), a.getDayOfWeek(), a.getStartTime(), a.getEndTime()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AvailabilityRangeResponse> getTeacherAvailability() {
        return teacherAvailabilityRepository.findAll().stream()
                .map(a -> new AvailabilityRangeResponse(a.getId(), a.getDayOfWeek(), a.getStartTime(), a.getEndTime()))
                .toList();
    }
}