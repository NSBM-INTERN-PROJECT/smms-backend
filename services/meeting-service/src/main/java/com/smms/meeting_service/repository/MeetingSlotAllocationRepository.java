package com.smms.meeting_service.repository;

import com.smms.meeting_service.domain.MeetingSlotAllocation;
import com.smms.meeting_service.domain.SlotAllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingSlotAllocationRepository extends JpaRepository<MeetingSlotAllocation, Long> {
    List<MeetingSlotAllocation> findBySlotId(Long slotId);
    Optional<MeetingSlotAllocation> findBySlotIdAndStudentUserId(Long slotId, Long studentUserId);
    List<MeetingSlotAllocation> findByStudentUserIdAndStatus(Long studentUserId, SlotAllocationStatus status);
}
