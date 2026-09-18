package com.smms.user.repository;

import com.smms.user.domain.DataCollectionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataCollectionRequestRepository extends JpaRepository<DataCollectionRequest, Long> {
    Page<DataCollectionRequest> findByMentorUserIdOrderByCreatedAtDesc(Long mentorUserId, Pageable pageable);
}
