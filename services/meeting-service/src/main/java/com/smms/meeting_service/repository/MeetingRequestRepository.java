package com.smms.meeting_service.repository;

import com.smms.meeting_service.domain.MeetingRequest;
import com.smms.meeting_service.domain.MeetingRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRequestRepository extends JpaRepository<MeetingRequest, Long> {
    Page<MeetingRequest> findByMentorUserIdAndStatusOrderByCreatedAtAsc(Long mentorUserId, MeetingRequestStatus status, Pageable pageable);
    Page<MeetingRequest> findByStudentUserIdOrderByCreatedAtDesc(Long studentUserId, Pageable pageable);
}
