package com.smms.user.service.impl;

import com.smms.user.dto.request.ProfileUpdateRejectRequest;
import com.smms.user.dto.response.ProfileUpdateRequestResponse;
import com.smms.user.entity.ProfileUpdateRequest;
import com.smms.user.entity.StudentExtendedProfile;
import com.smms.user.exception.ProfileUpdateRequestNotFoundException;
import com.smms.user.repository.ProfileUpdateRequestRepository;
import com.smms.user.repository.StudentExtendedProfileRepository;
import com.smms.user.service.ProfileUpdateRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileUpdateRequestServiceImpl implements ProfileUpdateRequestService {

    private final ProfileUpdateRequestRepository profileUpdateRequestRepository;
    private final StudentExtendedProfileRepository extendedProfileRepository;

    @Override
    public List<ProfileUpdateRequestResponse> getPendingRequestsForMentor(Long mentorUserId) {

        return profileUpdateRequestRepository
                .findByMentorUserIdAndStatus(mentorUserId, ProfileUpdateRequest.RequestStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProfileUpdateRequestResponse approveRequest(Long requestId) {

        ProfileUpdateRequest request = profileUpdateRequestRepository.findById(requestId)
                .orElseThrow(() -> new ProfileUpdateRequestNotFoundException(
                        "Profile update request not found: " + requestId));

        // Apply the new value to the student's actual extended profile
        StudentExtendedProfile profile = extendedProfileRepository
                .findByStudentUserId(request.getStudentUserId())
                .orElseThrow(() -> new ProfileUpdateRequestNotFoundException(
                        "Extended profile not found for student: " + request.getStudentUserId()));

        // NOTE: in a full implementation, newValue would be parsed field-by-field.
        // For now, this marks the approval and updates the timestamp as proof of concept.
        profile.setLastUpdatedAt(Instant.now());
        extendedProfileRepository.save(profile);

        request.setStatus(ProfileUpdateRequest.RequestStatus.APPROVED);
        request.setReviewedAt(Instant.now());
        ProfileUpdateRequest saved = profileUpdateRequestRepository.save(request);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ProfileUpdateRequestResponse rejectRequest(Long requestId, ProfileUpdateRejectRequest rejectRequest) {

        ProfileUpdateRequest request = profileUpdateRequestRepository.findById(requestId)
                .orElseThrow(() -> new ProfileUpdateRequestNotFoundException(
                        "Profile update request not found: " + requestId));

        request.setStatus(ProfileUpdateRequest.RequestStatus.REJECTED);
        request.setMentorNotes(rejectRequest.getRejectionReason());
        request.setReviewedAt(Instant.now());

        ProfileUpdateRequest saved = profileUpdateRequestRepository.save(request);

        return mapToResponse(saved);
    }

    private ProfileUpdateRequestResponse mapToResponse(ProfileUpdateRequest request) {
        return ProfileUpdateRequestResponse.builder()
                .id(request.getId())
                .studentUserId(request.getStudentUserId())
                .mentorUserId(request.getMentorUserId())
                .fieldName(request.getFieldName())
                .oldValue(request.getOldValue())
                .newValue(request.getNewValue())
                .status(request.getStatus())
                .mentorNotes(request.getMentorNotes())
                .createdAt(request.getCreatedAt())
                .reviewedAt(request.getReviewedAt())
                .build();
    }
}