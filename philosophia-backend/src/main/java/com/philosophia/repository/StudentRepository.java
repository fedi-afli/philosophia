package com.philosophia.repository;

import com.philosophia.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository  extends JpaRepository<Student, Integer> {
    @Query("select s from Student s left join fetch s.section where s.user.id = :userId")
    Optional<Student> findByUserIdWithSection(@Param("userId") Long userId);
}
