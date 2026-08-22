package com.smms.user.dto.response;

import com.smms.user.domain.StudentExtendedProfile;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class ExtendedProfileResponse {
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
    private LocalDateTime submittedAt;
    private LocalDateTime lastUpdatedAt;

    public static ExtendedProfileResponse from(StudentExtendedProfile e) {
        return ExtendedProfileResponse.builder()
                .id(e.getId()).studentUserId(e.getStudentUserId())
                .parentName(e.getParentName()).parentPhone(e.getParentPhone())
                .parentEmail(e.getParentEmail()).homeDistrict(e.getHomeDistrict())
                .residenceAddress(e.getResidenceAddress())
                .emergencyContactName(e.getEmergencyContactName())
                .emergencyContactPhone(e.getEmergencyContactPhone())
                .formSubmitted(e.getFormSubmitted()).submittedAt(e.getSubmittedAt())
                .lastUpdatedAt(e.getLastUpdatedAt()).build();
    }
}
