package com.philosophia.repository;

import com.philosophia.models.TeacherAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherAvailabilityRepository extends JpaRepository<TeacherAvailability, Long> {
    List<TeacherAvailability> findAll(); // inherited, listed for clarity — no filter needed, single teacher
    void deleteAll(); // inherited too — full replace strategy, same as unavailability
}