package com.philosophia.models;

import com.philosophia.enums.TeachingPlanStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teaching_plans")
@Getter
@Setter
@NoArgsConstructor
public class TeachingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chapter_name", nullable = false, length = 255)
    private String chapterName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(name = "duration_weeks", nullable = false)
    private Integer durationWeeks;

    /**
     * Number of sessions required per student per week.
     *
     * Example:
     * 20 students + max 8 students/session
     * -> scheduler may create 3 sessions in the same week.
     *
     * This value describes the requirement for each student,
     * not the total number of sessions.
     */
    @Column(name = "sessions_per_week", nullable = false)
    private Integer sessionsPerWeek = 1;

    @Column(name = "session_duration_minutes", nullable = false)
    private Integer sessionDurationMinutes;

    @Column(name = "max_students", nullable = false)
    private Integer maxStudents;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeachingPlanStatus status = TeachingPlanStatus.DRAFT;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "teachingPlan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TeachingPlanStudent> students = new ArrayList<>();

    @OneToMany(
            mappedBy = "teachingPlan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Session> sessions = new ArrayList<>();
}