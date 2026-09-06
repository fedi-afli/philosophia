package com.philosophia.repository;

import com.philosophia.enums.UserRole;
import com.philosophia.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);
    long countByRole(UserRole role);
}
