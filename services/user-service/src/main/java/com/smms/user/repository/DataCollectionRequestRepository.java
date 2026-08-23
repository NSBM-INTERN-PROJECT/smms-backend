package com.smms.user.repository;

import com.smms.user.entity.DataCollectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataCollectionRequestRepository extends JpaRepository<DataCollectionRequest, Long> {

    List<DataCollectionRequest> findByMentorUserId(Long mentorUserId);
}