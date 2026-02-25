package com.jungle.courseshop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "lecturers")
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;

    @Column(columnDefinition = "TEXT")
    String bio;

    String address;

    String gender;

    String domainExpertise;

    @Column(nullable = false, unique = true)
    String identityNumber;

    @Enumerated(EnumType.STRING)
    LecturerStatus status;

    LocalDateTime registeredAt;

    Double averageRating;
    Integer totalStudents;
}
