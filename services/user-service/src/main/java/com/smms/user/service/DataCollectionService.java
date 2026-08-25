package com.smms.user.service;

import com.smms.user.domain.DataCollectionRecipient;
import com.smms.user.domain.RecipientStatus;
import com.smms.user.dto.request.DataCollectionRequest;
import com.smms.user.dto.response.DataCollectionResponse;
import com.smms.user.exception.RequestNotFoundException;
import com.smms.user.repository.DataCollectionRecipientRepository;
import com.smms.user.repository.DataCollectionRequestRepository;
import com.smms.user.repository.StudentProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class DataCollectionService {

    private final com.smms.user.repository.DataCollectionRequestRepository dcRequestRepo;
    private final DataCollectionRecipientRepository dcRecipientRepo;
    private final StudentProfileRepository studentRepo;
    private final ObjectMapper objectMapper;

    /**
     * Mentor creates a data-collection request.
     * Recipients are resolved by batch/department filter OR explicit student list.
     */
    @Transactional
    public DataCollectionResponse create(Long mentorUserId, DataCollectionRequest req) {
        // Resolve recipients
        List<Long> recipientIds;
        if (req.getStudentUserIds() != null && !req.getStudentUserIds().isEmpty()) {
            recipientIds = req.getStudentUserIds();
        } else if (req.getBatch() != null && req.getDepartment() != null) {
            recipientIds = studentRepo.findByBatchAndDepartmentAndIsActiveTrue(req.getBatch(), req.getDepartment())
                    .stream().map(s -> s.getUserId()).collect(Collectors.toList());
        } else if (req.getBatch() != null) {
            recipientIds = studentRepo.findByBatchAndIsActiveTrue(req.getBatch())
                    .stream().map(s -> s.getUserId()).collect(Collectors.toList());
        } else if (req.getDepartment() != null) {
            recipientIds = studentRepo.findByDepartmentAndIsActiveTrue(req.getDepartment())
                    .stream().map(s -> s.getUserId()).collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Provide at least one filter: batch, department, or studentUserIds");
        }

        // Persist filter criteria as JSON string
        String criteriaJson;
        try {
            criteriaJson = objectMapper.writeValueAsString(Map.of(
                    "batch", req.getBatch() != null ? req.getBatch() : "",
                    "department", req.getDepartment() != null ? req.getDepartment() : "",
                    "explicitIds", req.getStudentUserIds() != null ? req.getStudentUserIds() : List.of()));
        } catch (Exception e) { criteriaJson = "{}"; }

        // Save the request entity
        com.smms.user.domain.DataCollectionRequest entity = com.smms.user.domain.DataCollectionRequest.builder()
                .mentorUserId(mentorUserId)
                .filterCriteria(criteriaJson)
                .message(req.getMessage())
                .build();
        entity = dcRequestRepo.save(entity);

        // Save recipients
        final Long requestId = entity.getId();
        List<DataCollectionRecipient> recipients = recipientIds.stream()
                .map(sid -> DataCollectionRecipient.builder()
                        .requestId(requestId).studentUserId(sid)
                        .status(RecipientStatus.PENDING).build())
                .collect(Collectors.toList());
        dcRecipientRepo.saveAll(recipients);

        log.info("Data collection request {} created for {} recipients", requestId, recipients.size());
        return DataCollectionResponse.from(entity, recipients.size());
    }

    @Transactional(readOnly = true)
    public DataCollectionResponse getById(Long id) {
        var entity = dcRequestRepo.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(id));
        int total = dcRecipientRepo.findByRequestId(id).size();
        return DataCollectionResponse.from(entity, total);
    }

    /** Student marks their data-collection task as submitted. */
    @Transactional
    public void markSubmitted(Long requestId, Long studentUserId) {
        int updated = dcRecipientRepo.markAsSubmitted(requestId, studentUserId);
        if (updated == 0)
            throw new RequestNotFoundException(requestId);
    }
}
