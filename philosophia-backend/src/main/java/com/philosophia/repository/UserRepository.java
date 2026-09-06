package com.philosophia.repository;

import com.philosophia.enums.UserRole;
import com.philosophia.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    long countByRole(UserRole role);

    @Query("select u.active from User u where u.id = :id")
    Optional<Boolean> isActive(@Param("id") Long id);
}