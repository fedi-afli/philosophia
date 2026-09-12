// SessionStudentRepository
package com.philosophia.repository;

import com.philosophia.models.SessionStudent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionStudentRepository extends JpaRepository<SessionStudent, Long> {
}