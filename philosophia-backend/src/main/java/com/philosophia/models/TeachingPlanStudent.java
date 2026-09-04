package com.philosophia.models;

import com.philosophia.enums.TeachingPlanStudentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "teaching_plan_students",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_plan_student",
                        columnNames = {"teaching_plan_id", "student_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class TeachingPlanStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teaching_plan_id", nullable = false)
    private TeachingPlan teachingPlan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeachingPlanStudentStatus status =
            TeachingPlanStudentStatus.ENROLLED;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;
}