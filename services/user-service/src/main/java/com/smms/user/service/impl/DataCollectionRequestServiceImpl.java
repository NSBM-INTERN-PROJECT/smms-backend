package com.smms.user.service.impl;

import com.smms.user.dto.request.DataCollectionRequestCreate;
import com.smms.user.dto.response.DataCollectionRequestResponse;
import com.smms.user.entity.DataCollectionRecipient;
import com.smms.user.entity.DataCollectionRequest;
import com.smms.user.exception.StudentExtendedProfileNotFoundException;
import com.smms.user.repository.DataCollectionRecipientRepository;
import com.smms.user.repository.DataCollectionRequestRepository;
import com.smms.user.service.DataCollectionRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DataCollectionRequestServiceImpl implements DataCollectionRequestService {

    private final DataCollectionRequestRepository requestRepository;
    private final DataCollectionRecipientRepository recipientRepository;

    @Override
    @Transactional
    public DataCollectionRequestResponse createRequest(
            Long mentorUserId, DataCollectionRequestCreate request, List<Long> matchedStudentIds) {

        // Build a simple JSON-like string for the filter criteria
        String filterCriteria = String.format(
                "{\"department\":\"%s\",\"degreeProgram\":\"%s\",\"batch\":\"%s\"}",
                request.getDepartment(), request.getDegreeProgram(), request.getBatch());

        DataCollectionRequest newRequest = DataCollectionRequest.builder()
                .mentorUserId(mentorUserId)
                .filterCriteria(filterCriteria)
                .message(request.getMessage())
                .build();

        DataCollectionRequest savedRequest = requestRepository.save(newRequest);

        // Create one recipient row per matched student
        // (matchedStudentIds will come from a real filtered query once wired up)
        for (Long studentId : matchedStudentIds) {
            DataCollectionRecipient recipient = DataCollectionRecipient.builder()
                    .requestId(savedRequest.getId())
                    .studentUserId(studentId)
                    .build();
            recipientRepository.save(recipient);
        }

        return mapToResponse(savedRequest);
    }

    @Override
    public List<DataCollectionRequestResponse> getMyRequests(Long mentorUserId) {
        return requestRepository.findByMentorUserId(mentorUserId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public DataCollectionRequestResponse getRequestWithRecipientStatus(Long requestId) {
        DataCollectionRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new StudentExtendedProfileNotFoundException(
                        "Data collection request not found: " + requestId));

        return mapToResponse(request);
    }

    private DataCollectionRequestResponse mapToResponse(DataCollectionRequest request) {

        List<DataCollectionRecipient> recipients = recipientRepository.findByRequestId(request.getId());

        long submitted = recipients.stream()
                .filter(r -> r.getStatus() == DataCollectionRecipient.RecipientStatus.SUBMITTED)
                .count();
        long pending = recipients.size() - submitted;

        return DataCollectionRequestResponse.builder()
                .id(request.getId())
                .mentorUserId(request.getMentorUserId())
                .filterCriteria(request.getFilterCriteria())
                .message(request.getMessage())
                .createdAt(request.getCreatedAt())
                .totalRecipients(recipients.size())
                .submittedCount((int) submitted)
                .pendingCount((int) pending)
                .build();
    }
}