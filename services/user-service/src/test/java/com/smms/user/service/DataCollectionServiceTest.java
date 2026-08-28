package com.smms.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smms.user.domain.RecipientStatus;
import com.smms.user.domain.StudentProfile;
import com.smms.user.dto.request.DataCollectionRequest;
import com.smms.user.dto.response.DataCollectionResponse;
import com.smms.user.exception.RequestNotFoundException;
import com.smms.user.repository.DataCollectionRecipientRepository;
import com.smms.user.repository.DataCollectionRequestRepository;
import com.smms.user.repository.StudentProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataCollectionServiceTest {

    @Mock
    private DataCollectionRequestRepository dcRequestRepo;

    @Mock
    private DataCollectionRecipientRepository dcRecipientRepo;

    @Mock
    private StudentProfileRepository studentRepo;

    @InjectMocks
    private DataCollectionService dataCollectionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private com.smms.user.domain.DataCollectionRequest savedEntity;

    @BeforeEach
    void setUp() {
        // Inject the real ObjectMapper into the service (since @InjectMocks won't auto-wire it)
        try {
            var field = DataCollectionService.class.getDeclaredField("objectMapper");
            field.setAccessible(true);
            field.set(dataCollectionService, objectMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        savedEntity = com.smms.user.domain.DataCollectionRequest.builder()
                .id(1L)
                .mentorUserId(10L)
                .filterCriteria("{}")
                .message("Please fill in your form")
                .build();
    }

    // ─── create() tests ───────────────────────────────────────

    @Test
    void create_shouldResolveRecipientsByBatchAndDepartment() {
        DataCollectionRequest req = new DataCollectionRequest();
        req.setBatch("2022");
        req.setDepartment("Computing");
        req.setMessage("Please fill in your form");

        StudentProfile student = StudentProfile.builder().userId(20L).build();
        when(studentRepo.findByBatchAndDepartmentAndIsActiveTrue("2022", "Computing"))
                .thenReturn(List.of(student));
        when(dcRequestRepo.save(any())).thenReturn(savedEntity);

        DataCollectionResponse response = dataCollectionService.create(10L, req);

        assertThat(response).isNotNull();
        verify(dcRecipientRepo, times(1)).saveAll(anyList());
    }

    @Test
    void create_shouldUseExplicitStudentIds_whenProvided() {
        DataCollectionRequest req = new DataCollectionRequest();
        req.setStudentUserIds(List.of(20L, 21L, 22L));
        req.setMessage("Please fill in your form");

        when(dcRequestRepo.save(any())).thenReturn(savedEntity);

        DataCollectionResponse response = dataCollectionService.create(10L, req);

        assertThat(response).isNotNull();
        verify(studentRepo, never()).findByBatchAndDepartmentAndIsActiveTrue(any(), any());
        verify(dcRecipientRepo, times(1)).saveAll(anyList());
    }

    @Test
    void create_shouldFilterByBatchOnly_whenOnlyBatchProvided() {
        DataCollectionRequest req = new DataCollectionRequest();
        req.setBatch("2022");

        StudentProfile student = StudentProfile.builder().userId(20L).build();
        when(studentRepo.findByBatchAndIsActiveTrue("2022")).thenReturn(List.of(student));
        when(dcRequestRepo.save(any())).thenReturn(savedEntity);

        dataCollectionService.create(10L, req);

        verify(studentRepo, times(1)).findByBatchAndIsActiveTrue("2022");
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenNoFilterProvided() {
        DataCollectionRequest req = new DataCollectionRequest();
        // no batch, no department, no studentUserIds

        assertThatThrownBy(() -> dataCollectionService.create(10L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Provide at least one filter");

        verify(dcRequestRepo, never()).save(any());
    }

    // ─── getById() tests ──────────────────────────────────────

    @Test
    void getById_shouldReturnResponse_whenRequestExists() {
        when(dcRequestRepo.findById(1L)).thenReturn(Optional.of(savedEntity));
        when(dcRecipientRepo.findByRequestId(1L)).thenReturn(List.of());

        DataCollectionResponse response = dataCollectionService.getById(1L);

        assertThat(response).isNotNull();
    }

    @Test
    void getById_shouldThrowRequestNotFoundException_whenRequestDoesNotExist() {
        when(dcRequestRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dataCollectionService.getById(999L))
                .isInstanceOf(RequestNotFoundException.class);
    }

    // ─── markSubmitted() tests ────────────────────────────────

    @Test
    void markSubmitted_shouldSucceed_whenRecipientExists() {
        when(dcRecipientRepo.markAsSubmitted(1L, 20L)).thenReturn(1);

        dataCollectionService.markSubmitted(1L, 20L);

        verify(dcRecipientRepo, times(1)).markAsSubmitted(1L, 20L);
    }

    @Test
    void markSubmitted_shouldThrowRequestNotFoundException_whenRecipientDoesNotExist() {
        when(dcRecipientRepo.markAsSubmitted(999L, 20L)).thenReturn(0);

        assertThatThrownBy(() -> dataCollectionService.markSubmitted(999L, 20L))
                .isInstanceOf(RequestNotFoundException.class);
    }
}
