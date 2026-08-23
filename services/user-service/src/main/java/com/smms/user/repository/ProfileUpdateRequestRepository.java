package com.smms.user.repository;

import com.smms.user.entity.ProfileUpdateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileUpdateRequestRepository extends JpaRepository<ProfileUpdateRequest, Long> {

    List<ProfileUpdateRequest> findByMentorUserIdAndStatus(
            Long mentorUserId, ProfileUpdateRequest.RequestStatus status);

    List<ProfileUpdateRequest> findByStudentUserId(Long studentUserId);
}
