package com.smms.meeting_service.repository;

import com.smms.meeting_service.domain.MeetingSlot;
import com.smms.meeting_service.domain.SlotStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeetingSlotRepository extends JpaRepository<MeetingSlot, Long> {
    List<MeetingSlot> findByMentorUserIdAndStatusOrderBySlotDateAscStartTimeAsc(Long mentorUserId, SlotStatus status);
    Page<MeetingSlot> findByMentorUserIdOrderBySlotDateDescStartTimeDesc(Long mentorUserId, Pageable pageable);
    List<MeetingSlot> findByMentorUserIdAndSlotDateAndStatus(Long mentorUserId, LocalDate date, SlotStatus status);
    List<MeetingSlot> findByStatusAndSlotDateAfterOrderBySlotDateAscStartTimeAsc(SlotStatus status, LocalDate date);
}
