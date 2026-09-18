package com.smms.meeting_service.repository;

import com.smms.meeting_service.domain.Meeting;
import com.smms.meeting_service.domain.MeetingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Page<Meeting> findByMentorUserIdOrderByScheduledDateDesc(Long mentorUserId, Pageable pageable);
    Page<Meeting> findByStudentUserIdOrderByScheduledDateDesc(Long studentUserId, Pageable pageable);
    List<Meeting> findByMentorUserIdAndScheduledDate(Long mentorUserId, LocalDate date);
    List<Meeting> findByMentorUserIdAndStatusAndScheduledDateAfter(Long mentorUserId, MeetingStatus status, LocalDate date);
    List<Meeting> findByStudentUserIdAndStatusAndScheduledDateAfter(Long studentUserId, MeetingStatus status, LocalDate date);
    @Query("SELECT COUNT(m) FROM Meeting m WHERE m.mentorUserId = :mentorUserId AND m.status = 'COMPLETED'")
    long countCompletedByMentor(Long mentorUserId);
}
