package com.philosophia.services;

import com.philosophia.dto.CreateStudentRequest;
import com.philosophia.dto.StudentCountResponse;
import com.philosophia.dto.StudentResponse;
import com.philosophia.enums.UserRole;
import com.philosophia.models.Student;
import com.philosophia.models.User;
import com.philosophia.repository.StudentRepository;
import com.philosophia.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public StudentCountResponse getStudentCount(){
        return new StudentCountResponse(this.userRepository.countByRole(UserRole.STUDENT));



    }


    public StudentResponse addStudent(CreateStudentRequest req){
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
        return new StudentResponse(student.getId(),user.getUsername(),student.getFirstName(),student.getLastName(),"","","","",0);

    }
}
