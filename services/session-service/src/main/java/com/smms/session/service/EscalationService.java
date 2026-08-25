package com.smms.session.service;

import com.smms.session.domain.Escalation;
import com.smms.session.domain.EscalationCategory;
import com.smms.session.domain.EscalationStatus;
import com.smms.session.dto.request.CreateEscalationRequest;
import com.smms.session.dto.request.UpdateEscalationStatusRequest;
import com.smms.session.dto.response.EscalationResponse;
import com.smms.session.dto.response.PagedResponse;
import com.smms.session.exception.EscalationNotFoundException;
import com.smms.session.exception.SessionNoteNotFoundException;
import com.smms.session.repository.EscalationRepository;
import com.smms.session.repository.SessionNoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor @Slf4j
public class EscalationService {

    private final EscalationRepository escalationRepo;
    private final SessionNoteRepository noteRepo;

    /**
     * Mentor raises an escalation linked to a session note.
     * The session note must exist and belong to this mentor.
     */
    @Transactional
    public EscalationResponse create(Long mentorUserId, CreateEscalationRequest req) {
        // Validate session note exists
        noteRepo.findById(req.getSessionNoteId())
                .orElseThrow(() -> new SessionNoteNotFoundException(req.getSessionNoteId()));

        Escalation escalation = Escalation.builder()
                .sessionNoteId(req.getSessionNoteId())
                .mentorUserId(mentorUserId)
                .studentUserId(req.getStudentUserId())
                .category(req.getCategory())
                .description(req.getDescription())
                .escalatedToRole(req.getEscalatedToRole())
                .escalatedToUserId(req.getEscalatedToUserId())
                .status(EscalationStatus.OPEN)
                .build();

        EscalationResponse response = EscalationResponse.from(escalationRepo.save(escalation));
        log.info("Escalation {} created for student {} by mentor {}",
                response.getId(), req.getStudentUserId(), mentorUserId);
        return response;
    }

    /** List all escalations with optional status and category filters (Admin/Coordinator). */
    @Transactional(readOnly = true)
    public PagedResponse<EscalationResponse> listAll(int page, int size,
                                                       EscalationStatus status,
                                                       EscalationCategory category) {
        return PagedResponse.from(
                escalationRepo.findByFilters(status, category,
                        PageRequest.of(page, size, Sort.by("createdAt").descending())),
                EscalationResponse::from);
    }

    /** All escalations for a specific student. */
    @Transactional(readOnly = true)
    public PagedResponse<EscalationResponse> getForStudent(Long studentUserId, int page, int size) {
        return PagedResponse.from(
                escalationRepo.findByStudentUserIdOrderByCreatedAtDesc(
                        studentUserId, PageRequest.of(page, size)),
                EscalationResponse::from);
    }

    /** Mentor's own raised escalations. */
    @Transactional(readOnly = true)
    public PagedResponse<EscalationResponse> getForMentor(Long mentorUserId, int page, int size) {
        return PagedResponse.from(
                escalationRepo.findByMentorUserIdOrderByCreatedAtDesc(
                        mentorUserId, PageRequest.of(page, size)),
                EscalationResponse::from);
    }

    /** Update escalation status — Coordinator/Admin action. */
    @Transactional
    public EscalationResponse updateStatus(Long escalationId, UpdateEscalationStatusRequest req) {
        Escalation esc = escalationRepo.findById(escalationId)
                .orElseThrow(() -> new EscalationNotFoundException(escalationId));

        esc.setStatus(req.getStatus());
        if (req.getResolutionNotes() != null) esc.setResolutionNotes(req.getResolutionNotes());
        if (req.getStatus() == EscalationStatus.RESOLVED || req.getStatus() == EscalationStatus.CLOSED) {
            esc.setResolvedAt(LocalDateTime.now());
        }

        return EscalationResponse.from(escalationRepo.save(esc));
    }
}
