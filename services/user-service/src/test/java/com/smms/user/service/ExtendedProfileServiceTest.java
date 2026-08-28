package com.smms.user.service;

import com.smms.user.domain.StudentExtendedProfile;
import com.smms.user.dto.request.ExtendedProfileRequest;
import com.smms.user.dto.response.ExtendedProfileResponse;
import com.smms.user.repository.StudentExtendedProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtendedProfileServiceTest {

    @Mock
    private StudentExtendedProfileRepository extRepo;

    @InjectMocks
    private ExtendedProfileService extendedProfileService;

    private ExtendedProfileRequest request;
    private StudentExtendedProfile existingProfile;

    @BeforeEach
    void setUp() {
        request = new ExtendedProfileRequest();
        request.setParentName("Nimal Perera");
        request.setParentPhone("0712345678");
        request.setParentEmail("nimal@test.com");
        request.setHomeDistrict("Colombo");
        request.setResidenceAddress("No 12, Main Street, Colombo");
        request.setEmergencyContactName("Sunil Perera");
        request.setEmergencyContactPhone("0719876543");

        existingProfile = StudentExtendedProfile.builder()
                .id(1L)
                .studentUserId(20L)
                .parentName("Old Name")
                .residenceAddress("Old Address")
                .formSubmitted(true)
                .build();
    }

    // ─── get() tests ──────────────────────────────────────────

    @Test
    void get_shouldReturnExistingProfile_whenProfileExists() {
        when(extRepo.findByStudentUserId(20L)).thenReturn(Optional.of(existingProfile));

        ExtendedProfileResponse response = extendedProfileService.get(20L);

        assertThat(response.getParentName()).isEqualTo("Old Name");
        assertThat(response.getFormSubmitted()).isTrue();
    }

    @Test
    void get_shouldReturnEmptyUnsubmittedProfile_whenProfileDoesNotExist() {
        when(extRepo.findByStudentUserId(999L)).thenReturn(Optional.empty());

        ExtendedProfileResponse response = extendedProfileService.get(999L);

        assertThat(response.getFormSubmitted()).isFalse();
    }

    // ─── submit() tests ───────────────────────────────────────

    @Test
    void submit_shouldCreateNewProfile_whenNoneExists() {
        when(extRepo.findByStudentUserId(30L)).thenReturn(Optional.empty());
        when(extRepo.save(any(StudentExtendedProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExtendedProfileResponse response = extendedProfileService.submit(30L, request);

        assertThat(response.getParentName()).isEqualTo("Nimal Perera");
        assertThat(response.getFormSubmitted()).isTrue();
    }

    @Test
    void submit_shouldSetFormSubmittedTrueAndTimestamp_onFirstSubmission() {
        StudentExtendedProfile freshProfile = StudentExtendedProfile.builder()
                .studentUserId(30L)
                .formSubmitted(false)
                .build();

        when(extRepo.findByStudentUserId(30L)).thenReturn(Optional.of(freshProfile));

        ArgumentCaptor<StudentExtendedProfile> captor = ArgumentCaptor.forClass(StudentExtendedProfile.class);
        when(extRepo.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        extendedProfileService.submit(30L, request);

        StudentExtendedProfile saved = captor.getValue();
        assertThat(saved.getFormSubmitted()).isTrue();
        assertThat(saved.getSubmittedAt()).isNotNull();
    }

    @Test
    void submit_shouldUpdateExistingProfile_whenAlreadySubmitted() {
        when(extRepo.findByStudentUserId(20L)).thenReturn(Optional.of(existingProfile));
        when(extRepo.save(any(StudentExtendedProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExtendedProfileResponse response = extendedProfileService.submit(20L, request);

        assertThat(response.getParentName()).isEqualTo("Nimal Perera"); // updated
        assertThat(response.getResidenceAddress()).isEqualTo("No 12, Main Street, Colombo"); // updated
        verify(extRepo, times(1)).save(any(StudentExtendedProfile.class));
    }
}