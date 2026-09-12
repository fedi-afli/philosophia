package com.philosophia.repository;

import com.philosophia.enums.SectionEnum;
import com.philosophia.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository  extends JpaRepository<Student, Integer> {

    Optional<Student> findByUserId(Long id);
    Optional<Student> findById(Long id);
    // add to existing StudentRepository
    List<Student> findBySection(SectionEnum section);
}
