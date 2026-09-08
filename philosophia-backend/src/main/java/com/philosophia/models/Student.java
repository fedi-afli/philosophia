package com.philosophia.models;

import com.philosophia.enums.SectionEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String institute;

    @Enumerated(EnumType.STRING)
    @Column(name = "section", length = 50)
    private SectionEnum section;

    @Column(name = "unpaid_sessions_count", nullable = false)
    private Integer unpaidSessionsCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentUnavailability> unavailability = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<TeachingPlanStudent> teachingPlans = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<SessionStudent> sessions = new ArrayList<>();
}