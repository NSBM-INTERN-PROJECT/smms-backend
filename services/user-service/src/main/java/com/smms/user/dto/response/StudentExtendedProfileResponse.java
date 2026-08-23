package com.smms.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentExtendedProfileResponse {

    private Long id;
    private Long studentUserId;
    private String parentName;
    private String parentPhone;
    private String parentEmail;
    private String homeDistrict;
    private String residenceAddress;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private Boolean formSubmitted;
    private Instant submittedAt;
    private Instant lastUpdatedAt;
    private Boolean hasPendingChangeRequest; // true if a ProfileUpdateRequest is PENDING
}