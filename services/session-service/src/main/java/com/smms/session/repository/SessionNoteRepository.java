package com.smms.session.repository;

import com.smms.session.domain.ProgressStatus;
import com.smms.session.domain.SessionNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionNoteRepository extends JpaRepository<SessionNote, Long> {

    Optional<SessionNote> findByMeetingId(Long meetingId);
    boolean existsByMeetingId(Long meetingId);

    /** All notes for a student — respects isPrivate flag for non-mentor viewers. */
    Page<SessionNote> findByStudentUserIdOrderByCreatedAtDesc(
        Long studentUserId, Pageable pageable);

    /** Notes visible to non-mentor (only public notes). */
    Page<SessionNote> findByStudentUserIdAndIsPrivateFalseOrderByCreatedAtDesc(
        Long studentUserId, Pageable pageable);

    /** Mentor's notes for a student. */
    Page<SessionNote> findByMentorUserIdAndStudentUserIdOrderByCreatedAtDesc(
        Long mentorUserId, Long studentUserId, Pageable pageable);

    /** Count AT_RISK / CRITICAL students for a mentor — for report summaries. */
    @Query("SELECT COUNT(DISTINCT s.studentUserId) FROM SessionNote s " +
           "WHERE s.mentorUserId = :mentorUserId " +
           "AND (s.progressStatus = com.smms.session.domain.ProgressStatus.AT_RISK " +
           "  OR s.progressStatus = com.smms.session.domain.ProgressStatus.CRITICAL)")
    long countAtRiskStudentsByMentor(Long mentorUserId);

    /** Latest note per student — used for summary dashboards. */
    @Query("SELECT s FROM SessionNote s WHERE s.mentorUserId = :mentorUserId AND s.createdAt = (SELECT MAX(s2.createdAt) FROM SessionNote s2 WHERE s2.studentUserId = s.studentUserId AND s2.mentorUserId = :mentorUserId)")
    List<SessionNote> findLatestNotePerStudentForMentor(Long mentorUserId);

    List<SessionNote> findByStudentUserIdAndProgressStatus(Long studentUserId, ProgressStatus status);
}
