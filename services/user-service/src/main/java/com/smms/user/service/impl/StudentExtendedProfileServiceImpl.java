package com.smms.user.service.impl;

import com.smms.user.dto.request.StudentExtendedProfileRequest;
import com.smms.user.dto.response.StudentExtendedProfileResponse;
import com.smms.user.entity.ProfileUpdateRequest;
import com.smms.user.entity.StudentExtendedProfile;
import com.smms.user.exception.StudentExtendedProfileNotFoundException;
import com.smms.user.repository.ProfileUpdateRequestRepository;
import com.smms.user.repository.StudentExtendedProfileRepository;
import com.smms.user.service.StudentExtendedProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentExtendedProfileServiceImpl implements StudentExtendedProfileService {

    private final StudentExtendedProfileRepository extendedProfileRepository;
    private final ProfileUpdateRequestRepository profileUpdateRequestRepository;

    // NOTE: mentorUserId is passed in for now (normally resolved via allocation-service Feign call)
    @Override
    @Transactional
    public StudentExtendedProfileResponse submitOrUpdateExtendedProfile(
            Long studentUserId, Long mentorUserId, StudentExtendedProfileRequest request) {

        var existingOpt = extendedProfileRepository.findByStudentUserId(studentUserId);

        // CASE 1: First-time submission — save directly, no approval needed
        if (existingOpt.isEmpty()) {
            StudentExtendedProfile newProfile = StudentExtendedProfile.builder()
                    .studentUserId(studentUserId)
                    .parentName(request.getParentName())
                    .parentPhone(request.getParentPhone())
                    .parentEmail(request.getParentEmail())
                    .homeDistrict(request.getHomeDistrict())
                    .residenceAddress(request.getResidenceAddress())
                    .emergencyContactName(request.getEmergencyContactName())
                    .emergencyContactPhone(request.getEmergencyContactPhone())
                    .formSubmitted(true)
                    .build();

            StudentExtendedProfile saved = extendedProfileRepository.save(newProfile);
            return mapToResponse(saved, false);
        }

        // CASE 2: Already submitted before — create a pending approval request instead
        // (Does NOT touch the existing extended profile data yet)
        StudentExtendedProfile existing = existingOpt.get();

        ProfileUpdateRequest updateRequest = ProfileUpdateRequest.builder()
                .studentUserId(studentUserId)
                .mentorUserId(mentorUserId)
                .fieldName("extended_profile")
                .oldValue(existing.toString())
                .newValue(request.toString())
                .status(ProfileUpdateRequest.RequestStatus.PENDING)
                .build();

        profileUpdateRequestRepository.save(updateRequest);

        // Return current (unchanged) data, but flag that a request is pending
        return mapToResponse(existing, true);
    }

    @Override
    public StudentExtendedProfileResponse getOwnExtendedProfile(Long studentUserId) {

        StudentExtendedProfile profile = extendedProfileRepository.findByStudentUserId(studentUserId)
                .orElseThrow(() -> new StudentExtendedProfileNotFoundException(
                        "Extended profile not yet submitted for userId: " + studentUserId));

        boolean hasPending = !profileUpdateRequestRepository
                .findByStudentUserId(studentUserId).isEmpty();

        return mapToResponse(profile, hasPending);
    }

    private StudentExtendedProfileResponse mapToResponse(StudentExtendedProfile profile, boolean hasPending) {
        return StudentExtendedProfileResponse.builder()
                .id(profile.getId())
                .studentUserId(profile.getStudentUserId())
                .parentName(profile.getParentName())
                .parentPhone(profile.getParentPhone())
                .parentEmail(profile.getParentEmail())
                .homeDistrict(profile.getHomeDistrict())
                .residenceAddress(profile.getResidenceAddress())
                .emergencyContactName(profile.getEmergencyContactName())
                .emergencyContactPhone(profile.getEmergencyContactPhone())
                .formSubmitted(profile.getFormSubmitted())
                .submittedAt(profile.getSubmittedAt())
                .lastUpdatedAt(profile.getLastUpdatedAt())
                .hasPendingChangeRequest(hasPending)
                .build();
    }
}