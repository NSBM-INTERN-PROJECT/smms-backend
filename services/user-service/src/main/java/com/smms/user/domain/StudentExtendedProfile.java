package com.smms.user.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_extended_profiles")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentExtendedProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_user_id", nullable = false, unique = true)
    private Long studentUserId;

    @Column(name = "parent_name", length = 100)
    private String parentName;

    @Column(name = "parent_phone", length = 20)
    private String parentPhone;

    @Column(name = "parent_email", length = 100)
    private String parentEmail;

    @Column(name = "home_district", length = 100)
    private String homeDistrict;

    @Column(name = "residence_address", columnDefinition = "TEXT")
    private String residenceAddress;

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "form_submitted", nullable = false)
    @Builder.Default
    private Boolean formSubmitted = false;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @PrePersist
    protected void onCreate() { lastUpdatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { lastUpdatedAt = LocalDateTime.now(); }
}
