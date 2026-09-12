package com.philosophia.models;

import com.philosophia.enums.SectionEnum;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SectionEnum section;

    @Column(name = "duration_weeks", nullable = false)
    private Integer durationWeeks;

    @Column(name = "sessions_per_week", nullable = false)
    private Integer sessionsPerWeek = 1;

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

    @Override
    public String toString() {
        return "TeachingPlan{" +
                "id=" + id +
                ", chapter=" + chapter +
                ", section=" + section +
                ", durationWeeks=" + durationWeeks +
                ", sessionsPerWeek=" + sessionsPerWeek +
                ", maxStudents=" + maxStudents +
                ", startDate=" + startDate +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", sessions=" + sessions +
                '}';
    }

    @OneToMany(
            mappedBy = "teachingPlan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Session> sessions = new ArrayList<>();
}