package com.smms.user.service;

import com.smms.user.domain.RiskStatus;
import com.smms.user.domain.StudentProfile;
import com.smms.user.dto.request.CreateStudentProfileRequest;
import com.smms.user.dto.request.UpdateStudentProfileRequest;
import com.smms.user.dto.response.StudentProfileResponse;
import com.smms.user.exception.DuplicateProfileException;
import com.smms.user.exception.ProfileNotFoundException;
import com.smms.user.repository.StudentProfileRepository;
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
class StudentProfileServiceTest {

    @Mock
    private StudentProfileRepository studentRepo;

    @InjectMocks
    private StudentProfileService studentProfileService;

    private CreateStudentProfileRequest createRequest;
    private StudentProfile studentProfile;

    @BeforeEach
    void setUp() {
        createRequest = new CreateStudentProfileRequest();
        createRequest.setUserId(20L);
        createRequest.setFullName("Test Student");
        createRequest.setStudentId("FOC22999");
        createRequest.setEmail("teststudent@test.com");
        createRequest.setPhone("0777654321");
        createRequest.setDegreeProgram("BSc Software Engineering");
        createRequest.setDepartment("Computing");
        createRequest.setBatch("2022");
        createRequest.setIntake("Feb 2022");
        createRequest.setAcademicYear(3);

        studentProfile = StudentProfile.builder()
                .id(1L)
                .userId(20L)
                .fullName("Test Student")
                .studentId("FOC22999")
                .email("teststudent@test.com")
                .phone("0777654321")
                .degreeProgram("BSc Software Engineering")
                .department("Computing")
                .batch("2022")
                .intake("Feb 2022")
          
                .academicYear(3)
                .riskStatus(RiskStatus.LOW)
                .isActive(true)
                .build();
    }

    // ─── create() tests ───────────────────────────────────────

    @Test
    void create_shouldSaveAndReturnResponse_whenUserIdAndStudentIdAreUnique() {
        when(studentRepo.existsByUserId(20L)).thenReturn(false);
        when(studentRepo.existsByStudentId("FOC22999")).thenReturn(false);
        when(studentRepo.save(any(StudentProfile.class))).thenReturn(studentProfile);

        StudentProfileResponse response = studentProfileService.create(createRequest);

        assertThat(response).isNotNull();
        assertThat(response.getFullName()).isEqualTo("Test Student");
        assertThat(response.getStudentId()).isEqualTo("FOC22999");
        verify(studentRepo, times(1)).save(any(StudentProfile.class));
    }

    @Test
    void create_shouldThrowDuplicateProfileException_whenUserIdAlreadyExists() {
        when(studentRepo.existsByUserId(20L)).thenReturn(true);

        assertThatThrownBy(() -> studentProfileService.create(createRequest))
                .isInstanceOf(DuplicateProfileException.class)
                .hasMessageContaining("already exists");

        verify(studentRepo, never()).save(any());
    }

    @Test
    void create_shouldThrowDuplicateProfileException_whenStudentIdAlreadyInUse() {
        when(studentRepo.existsByUserId(20L)).thenReturn(false);
        when(studentRepo.existsByStudentId("FOC22999")).thenReturn(true);

        assertThatThrownBy(() -> studentProfileService.create(createRequest))
                .isInstanceOf(DuplicateProfileException.class)
                .hasMessageContaining("Student ID already in use");

        verify(studentRepo, never()).save(any());
    }

    // ─── getByUserId() tests ──────────────────────────────────

    @Test
    void getByUserId_shouldReturnResponse_whenProfileExists() {
        when(studentRepo.findByUserId(20L)).thenReturn(Optional.of(studentProfile));

        StudentProfileResponse response = studentProfileService.getByUserId(20L);

        assertThat(response.getUserId()).isEqualTo(20L);
        assertThat(response.getFullName()).isEqualTo("Test Student");
    }

    @Test
    void getByUserId_shouldThrowProfileNotFoundException_whenProfileDoesNotExist() {
        when(studentRepo.findByUserId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentProfileService.getByUserId(999L))
                .isInstanceOf(ProfileNotFoundException.class);
    }

    // ─── update() tests ───────────────────────────────────────

    @Test
    void update_shouldUpdateOnlyProvidedFields() {
        UpdateStudentProfileRequest updateRequest = new UpdateStudentProfileRequest();
        updateRequest.setDepartment("Data Science");
        updateRequest.setRiskStatus(RiskStatus.HIGH);

        when(studentRepo.findByUserId(20L)).thenReturn(Optional.of(studentProfile));
        when(studentRepo.save(any(StudentProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentProfileResponse response = studentProfileService.update(20L, updateRequest);

        assertThat(response.getDepartment()).isEqualTo("Data Science");
        assertThat(response.getRiskStatus()).isEqualTo(RiskStatus.HIGH);
        assertThat(response.getFullName()).isEqualTo("Test Student"); // unchanged
    }

    @Test
    void update_shouldThrowProfileNotFoundException_whenProfileDoesNotExist() {
        UpdateStudentProfileRequest updateRequest = new UpdateStudentProfileRequest();
        when(studentRepo.findByUserId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentProfileService.update(999L, updateRequest))
                .isInstanceOf(ProfileNotFoundException.class);

        verify(studentRepo, never()).save(any());
    }
}