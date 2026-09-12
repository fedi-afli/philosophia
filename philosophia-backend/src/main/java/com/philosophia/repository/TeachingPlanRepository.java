package com.philosophia.repository;

import com.philosophia.models.TeachingPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeachingPlanRepository extends JpaRepository<TeachingPlan, Long> {
}