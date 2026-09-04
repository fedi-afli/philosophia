package com.philosophia.models;

import com.philosophia.enums.AssignmentType;
import com.philosophia.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "session_students",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_session_student",
                        columnNames = {"session_id", "student_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SessionStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**
     * AUTO:
     * Assigned by the scheduling engine.
     *
     * MANUAL:
     * Assigned/transferred by the teacher.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_type", nullable = false)
    private AssignmentType assignmentType = AssignmentType.AUTO;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", nullable = false)
    private AttendanceStatus attendanceStatus =
            AttendanceStatus.PENDING;

    /**
     * If this assignment was created because
     * the student was absent from another session,
     * this points to the original session.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescheduled_from_session_id")
    private Session rescheduledFromSession;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
