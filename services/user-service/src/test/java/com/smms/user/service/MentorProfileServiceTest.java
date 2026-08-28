package com.smms.user.service;

import com.smms.user.domain.MentorProfile;
import com.smms.user.dto.request.CreateMentorProfileRequest;
import com.smms.user.dto.request.UpdateMentorProfileRequest;
import com.smms.user.dto.response.MentorProfileResponse;
import com.smms.user.exception.DuplicateProfileException;
import com.smms.user.exception.ProfileNotFoundException;
import com.smms.user.repository.MentorProfileRepository;
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
class MentorProfileServiceTest {

    @Mock
    private MentorProfileRepository mentorRepo;

    @InjectMocks
    private MentorProfileService mentorProfileService;

    private CreateMentorProfileRequest createRequest;
    private MentorProfile mentorProfile;

    @BeforeEach
    void setUp() {
        createRequest = new CreateMentorProfileRequest();
        createRequest.setUserId(10L);
        createRequest.setFullName("Test Mentor");
        createRequest.setEmployeeId("EMP010");
        createRequest.setDepartment("Computing");
        createRequest.setMaxStudents(10);

        mentorProfile = MentorProfile.builder()
                .id(1L)
                .userId(10L)
                .fullName("Test Mentor")
                .employeeId("EMP010")
                .department("Computing")
                .maxStudents(10)
                .isActive(true)
                .build();
    }

    // ─── create() tests ───────────────────────────────────────

    @Test
    void create_shouldSaveAndReturnResponse_whenUserIdAndEmployeeIdAreUnique() {
        when(mentorRepo.existsByUserId(10L)).thenReturn(false);
        when(mentorRepo.existsByEmployeeId("EMP010")).thenReturn(false);
        when(mentorRepo.save(any(MentorProfile.class))).thenReturn(mentorProfile);

        MentorProfileResponse response = mentorProfileService.create(createRequest);

        assertThat(response).isNotNull();
        assertThat(response.getFullName()).isEqualTo("Test Mentor");
        assertThat(response.getEmployeeId()).isEqualTo("EMP010");
        verify(mentorRepo, times(1)).save(any(MentorProfile.class));
    }

    @Test
    void create_shouldThrowDuplicateProfileException_whenUserIdAlreadyExists() {
        when(mentorRepo.existsByUserId(10L)).thenReturn(true);

        assertThatThrownBy(() -> mentorProfileService.create(createRequest))
                .isInstanceOf(DuplicateProfileException.class)
                .hasMessageContaining("already exists");

        verify(mentorRepo, never()).save(any());
    }

    @Test
    void create_shouldThrowDuplicateProfileException_whenEmployeeIdAlreadyInUse() {
        when(mentorRepo.existsByUserId(10L)).thenReturn(false);
        when(mentorRepo.existsByEmployeeId("EMP010")).thenReturn(true);

        assertThatThrownBy(() -> mentorProfileService.create(createRequest))
                .isInstanceOf(DuplicateProfileException.class)
                .hasMessageContaining("Employee ID already in use");

        verify(mentorRepo, never()).save(any());
    }

    @Test
    void create_shouldDefaultMaxStudentsToFive_whenNotProvided() {
        createRequest.setMaxStudents(null);
        when(mentorRepo.existsByUserId(10L)).thenReturn(false);
        when(mentorRepo.existsByEmployeeId("EMP010")).thenReturn(false);
        when(mentorRepo.save(any(MentorProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MentorProfileResponse response = mentorProfileService.create(createRequest);

        assertThat(response.getMaxStudents()).isEqualTo(5);
    }

    // ─── getByUserId() tests ──────────────────────────────────

    @Test
    void getByUserId_shouldReturnResponse_whenProfileExists() {
        when(mentorRepo.findByUserId(10L)).thenReturn(Optional.of(mentorProfile));

        MentorProfileResponse response = mentorProfileService.getByUserId(10L);

        assertThat(response.getUserId()).isEqualTo(10L);
        assertThat(response.getFullName()).isEqualTo("Test Mentor");
    }

    @Test
    void getByUserId_shouldThrowProfileNotFoundException_whenProfileDoesNotExist() {
        when(mentorRepo.findByUserId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentorProfileService.getByUserId(999L))
                .isInstanceOf(ProfileNotFoundException.class);
    }

    // ─── update() tests ───────────────────────────────────────

    @Test
    void update_shouldUpdateOnlyProvidedFields() {
        UpdateMentorProfileRequest updateRequest = new UpdateMentorProfileRequest();
        updateRequest.setDepartment("Data Science");
        updateRequest.setMaxStudents(15);

        when(mentorRepo.findByUserId(10L)).thenReturn(Optional.of(mentorProfile));
        when(mentorRepo.save(any(MentorProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MentorProfileResponse response = mentorProfileService.update(10L, updateRequest);

        assertThat(response.getDepartment()).isEqualTo("Data Science");
        assertThat(response.getMaxStudents()).isEqualTo(15);
        assertThat(response.getFullName()).isEqualTo("Test Mentor"); // unchanged
    }

    @Test
    void update_shouldThrowProfileNotFoundException_whenProfileDoesNotExist() {
        UpdateMentorProfileRequest updateRequest = new UpdateMentorProfileRequest();
        when(mentorRepo.findByUserId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentorProfileService.update(999L, updateRequest))
                .isInstanceOf(ProfileNotFoundException.class);

        verify(mentorRepo, never()).save(any());
    }
}