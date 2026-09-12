package com.philosophia.repository;

import com.philosophia.models.StudentUnavailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnavailabilityRepository extends JpaRepository<StudentUnavailability, Long> {
    List<StudentUnavailability> findByStudentId(Long studentId);
    void deleteByStudentId(Long studentId);
    List<StudentUnavailability> findByStudentIdIn(List<Long> studentIds);
}