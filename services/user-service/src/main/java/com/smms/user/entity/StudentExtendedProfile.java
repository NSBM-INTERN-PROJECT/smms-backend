package com.smms.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "student_extended_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentExtendedProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_user_id", nullable = false, unique = true)
    private Long studentUserId;

    @Column(name = "parent_name")
    private String parentName;

    @Column(name = "parent_phone")
    private String parentPhone;

    @Column(name = "parent_email")
    private String parentEmail;

    @Column(name = "home_district")
    private String homeDistrict;

    @Column(name = "residence_address", columnDefinition = "TEXT")
    private String residenceAddress;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Column(name = "form_submitted", nullable = false)
    private Boolean formSubmitted;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        submittedAt = now;
        lastUpdatedAt = now;
        if (formSubmitted == null) {
            formSubmitted = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = Instant.now();
    }
}