package com.smms.user.service;

import com.smms.user.domain.ProfileUpdateRequest;
import com.smms.user.domain.RequestStatus;
import com.smms.user.domain.StudentExtendedProfile;
import com.smms.user.dto.request.ProfileUpdateFieldRequest;
import com.smms.user.dto.request.ReviewUpdateRequest;
import com.smms.user.dto.response.PagedResponse;
import com.smms.user.dto.response.ProfileUpdateRequestResponse;
import com.smms.user.exception.AccessDeniedException;
import com.smms.user.exception.RequestNotFoundException;
import com.smms.user.repository.ProfileUpdateRequestRepository;
import com.smms.user.repository.StudentExtendedProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor @Slf4j
public class ProfileUpdateService {

    private final ProfileUpdateRequestRepository requestRepo;
    private final StudentExtendedProfileRepository extRepo;

    /**
     * Student submits a request to change a field in their extended profile.
     * The mentor assigned to them must approve it before the change is applied.
     */
    @Transactional
    public ProfileUpdateRequestResponse submitRequest(Long studentUserId, Long mentorUserId,
                                                      ProfileUpdateFieldRequest req) {
        // Capture the current value of the field
        String oldValue = extRepo.findByStudentUserId(studentUserId)
                .map(ext -> getFieldValue(ext, req.getFieldName()))
                .orElse(null);

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .studentUserId(studentUserId)
                .mentorUserId(mentorUserId)
                .fieldName(req.getFieldName())
                .oldValue(oldValue)
                .newValue(req.getNewValue())
                .status(RequestStatus.PENDING)
                .build();

        return ProfileUpdateRequestResponse.from(requestRepo.save(request));
    }

    /** Mentor sees all PENDING requests for their students. */
    @Transactional(readOnly = true)
    public PagedResponse<ProfileUpdateRequestResponse> getPendingForMentor(Long mentorUserId, int page, int size) {
        return PagedResponse.from(
                requestRepo.findByMentorUserIdAndStatus(
                        mentorUserId, RequestStatus.PENDING,
                        PageRequest.of(page, size, Sort.by("createdAt").ascending())),
                ProfileUpdateRequestResponse::from);
    }

    /** Student sees their own request history. */
    @Transactional(readOnly = true)
    public PagedResponse<ProfileUpdateRequestResponse> getStudentHistory(Long studentUserId, int page, int size) {
        return PagedResponse.from(
                requestRepo.findByStudentUserIdOrderByCreatedAtDesc(
                        studentUserId, PageRequest.of(page, size)),
                ProfileUpdateRequestResponse::from);
    }

    /** Mentor approves a pending request — field is applied immediately. */
    @Transactional
    public ProfileUpdateRequestResponse approve(Long requestId, Long mentorUserId, ReviewUpdateRequest review) {
        ProfileUpdateRequest request = getAndValidate(requestId, mentorUserId);
        request.setStatus(RequestStatus.APPROVED);
        request.setMentorNotes(review.getMentorNotes());
        request.setReviewedAt(LocalDateTime.now());
        requestRepo.save(request);

        // Apply the change
        StudentExtendedProfile ext = extRepo.findByStudentUserId(request.getStudentUserId())
                .orElseGet(() -> StudentExtendedProfile.builder()
                        .studentUserId(request.getStudentUserId()).formSubmitted(false).build());
        setFieldValue(ext, request.getFieldName(), request.getNewValue());
        extRepo.save(ext);

        log.info("Approved profile update {} for student {}", requestId, request.getStudentUserId());
        return ProfileUpdateRequestResponse.from(request);
    }

    /** Mentor rejects a pending request — no field change is made. */
    @Transactional
    public ProfileUpdateRequestResponse reject(Long requestId, Long mentorUserId, ReviewUpdateRequest review) {
        ProfileUpdateRequest request = getAndValidate(requestId, mentorUserId);
        request.setStatus(RequestStatus.REJECTED);
        request.setMentorNotes(review.getMentorNotes());
        request.setReviewedAt(LocalDateTime.now());
        return ProfileUpdateRequestResponse.from(requestRepo.save(request));
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    private ProfileUpdateRequest getAndValidate(Long requestId, Long mentorUserId) {
        ProfileUpdateRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        if (!request.getMentorUserId().equals(mentorUserId))
            throw new AccessDeniedException();
        if (request.getStatus() != RequestStatus.PENDING)
            throw new IllegalStateException("Request is already " + request.getStatus());
        return request;
    }

    private String getFieldValue(StudentExtendedProfile ext, String fieldName) {
        try {
            Field f = StudentExtendedProfile.class.getDeclaredField(camelCase(fieldName));
            f.setAccessible(true);
            Object val = f.get(ext);
            return val != null ? val.toString() : null;
        } catch (Exception e) { return null; }
    }

    private void setFieldValue(StudentExtendedProfile ext, String fieldName, String value) {
        try {
            Field f = StudentExtendedProfile.class.getDeclaredField(camelCase(fieldName));
            f.setAccessible(true);
            f.set(ext, value);
        } catch (Exception e) {
            log.warn("Could not apply field update for field: {}", fieldName);
        }
    }

    /** Converts snake_case to camelCase for reflection. */
    private String camelCase(String snake) {
        StringBuilder sb = new StringBuilder();
        boolean next = false;
        for (char c : snake.toCharArray()) {
            if (c == '_') { next = true; }
            else { sb.append(next ? Character.toUpperCase(c) : c); next = false; }
        }
        return sb.toString();
    }
}
