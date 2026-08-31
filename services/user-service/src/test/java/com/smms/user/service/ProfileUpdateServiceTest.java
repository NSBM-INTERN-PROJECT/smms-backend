package com.smms.user.service;

import com.smms.user.domain.ProfileUpdateRequest;
import com.smms.user.domain.RequestStatus;
import com.smms.user.domain.StudentExtendedProfile;
import com.smms.user.dto.request.ProfileUpdateFieldRequest;
import com.smms.user.dto.request.ReviewUpdateRequest;
import com.smms.user.dto.response.ProfileUpdateRequestResponse;
import com.smms.user.exception.AccessDeniedException;
import com.smms.user.exception.RequestNotFoundException;
import com.smms.user.repository.ProfileUpdateRequestRepository;
import com.smms.user.repository.StudentExtendedProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileUpdateServiceTest {

    @Mock
    private ProfileUpdateRequestRepository requestRepo;

    @Mock
    private StudentExtendedProfileRepository extRepo;

    @InjectMocks
    private ProfileUpdateService profileUpdateService;

    private ProfileUpdateFieldRequest fieldRequest;
    private ReviewUpdateRequest reviewRequest;
    private ProfileUpdateRequest pendingRequest;

    @BeforeEach
    void setUp() {
        fieldRequest = new ProfileUpdateFieldRequest();
        fieldRequest.setFieldName("residence_address");
        fieldRequest.setNewValue("No 45, New Street, Kandy");

        reviewRequest = new ReviewUpdateRequest();
        reviewRequest.setMentorNotes("Approved - verified");

        pendingRequest = ProfileUpdateRequest.builder()
                .id(1L)
                .studentUserId(20L)
                .mentorUserId(10L)
                .fieldName("residence_address")
                .oldValue("No 12, Main Street, Colombo")
                .newValue("No 45, New Street, Kandy")
                .status(RequestStatus.PENDING)
                .build();
    }

    // ─── submitRequest() tests ────────────────────────────────

    @Test
    void submitRequest_shouldCreatePendingRequest_withCapturedOldValue() {
        StudentExtendedProfile existing = StudentExtendedProfile.builder()
                .studentUserId(20L)
                .residenceAddress("No 12, Main Street, Colombo")
                .build();

        when(extRepo.findByStudentUserId(20L)).thenReturn(Optional.of(existing));
        when(requestRepo.save(any(ProfileUpdateRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        ProfileUpdateRequestResponse response =
                profileUpdateService.submitRequest(20L, 10L, fieldRequest);

        assertThat(response.getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(response.getNewValue()).isEqualTo("No 45, New Street, Kandy");
        verify(requestRepo, times(1)).save(any(ProfileUpdateRequest.class));
    }

    @Test
    void submitRequest_shouldSetOldValueNull_whenNoExtendedProfileExists() {
        when(extRepo.findByStudentUserId(99L)).thenReturn(Optional.empty());
        when(requestRepo.save(any(ProfileUpdateRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        ProfileUpdateRequestResponse response =
                profileUpdateService.submitRequest(99L, 10L, fieldRequest);

        assertThat(response.getOldValue()).isNull();
    }

    // ─── approve() tests ──────────────────────────────────────

    @Test
    void approve_shouldSetStatusApproved_andApplyFieldChange() {
        StudentExtendedProfile existing = StudentExtendedProfile.builder()
                .studentUserId(20L)
                .residenceAddress("No 12, Main Street, Colombo")
                .build();

        when(requestRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));
        when(requestRepo.save(any(ProfileUpdateRequest.class))).thenAnswer(inv -> inv.getArgument(0));
        when(extRepo.findByStudentUserId(20L)).thenReturn(Optional.of(existing));
        when(extRepo.save(any(StudentExtendedProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        ProfileUpdateRequestResponse response =
                profileUpdateService.approve(1L, 10L, reviewRequest);

        assertThat(response.getStatus()).isEqualTo(RequestStatus.APPROVED);
        verify(extRepo, times(1)).save(any(StudentExtendedProfile.class));
    }

    @Test
    void approve_shouldThrowAccessDeniedException_whenMentorDoesNotOwnRequest() {
        when(requestRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));

        assertThatThrownBy(() -> profileUpdateService.approve(1L, 999L, reviewRequest))
                .isInstanceOf(AccessDeniedException.class);

        verify(requestRepo, never()).save(any());
    }

    @Test
    void approve_shouldThrowRequestNotFoundException_whenRequestDoesNotExist() {
        when(requestRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileUpdateService.approve(999L, 10L, reviewRequest))
                .isInstanceOf(RequestNotFoundException.class);
    }

    @Test
    void approve_shouldThrowIllegalStateException_whenRequestAlreadyReviewed() {
        pendingRequest.setStatus(RequestStatus.APPROVED);
        when(requestRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));

        assertThatThrownBy(() -> profileUpdateService.approve(1L, 10L, reviewRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already");
    }

    // ─── reject() tests ───────────────────────────────────────

    @Test
    void reject_shouldSetStatusRejected_withoutApplyingFieldChange() {
        when(requestRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));
        when(requestRepo.save(any(ProfileUpdateRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        ProfileUpdateRequestResponse response =
                profileUpdateService.reject(1L, 10L, reviewRequest);

        assertThat(response.getStatus()).isEqualTo(RequestStatus.REJECTED);
        verify(extRepo, never()).save(any()); // field change never applied
    }

    @Test
    void reject_shouldThrowAccessDeniedException_whenMentorDoesNotOwnRequest() {
        when(requestRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));

        assertThatThrownBy(() -> profileUpdateService.reject(1L, 999L, reviewRequest))
                .isInstanceOf(AccessDeniedException.class);
    }
}