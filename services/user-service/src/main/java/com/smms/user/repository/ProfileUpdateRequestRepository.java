package com.smms.user.repository;

import com.smms.user.domain.ProfileUpdateRequest;
import com.smms.user.domain.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProfileUpdateRequestRepository extends JpaRepository<ProfileUpdateRequest, Long> {
    Page<ProfileUpdateRequest> findByMentorUserIdAndStatus(Long mentorUserId, RequestStatus status, Pageable pageable);
    Page<ProfileUpdateRequest> findByStudentUserIdOrderByCreatedAtDesc(Long studentUserId, Pageable pageable);
    List<ProfileUpdateRequest> findByStudentUserIdAndStatus(Long studentUserId, RequestStatus status);
}
