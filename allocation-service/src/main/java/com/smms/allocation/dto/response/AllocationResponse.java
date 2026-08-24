package com.smms.allocation.dto.response;

import com.smms.allocation.domain.Allocation;
import com.smms.allocation.domain.AllocationStatus;
import com.smms.allocation.domain.AllocationType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class AllocationResponse {
    private Long id;
    private Long mentorUserId;
    private Long studentUserId;
    private Long coordinatorUserId;
    private AllocationType allocationType;
    private AllocationStatus status;
    private LocalDate allocatedDate;
    private LocalDate deactivatedDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AllocationResponse from(Allocation a) {
        return AllocationResponse.builder()
                .id(a.getId())
                .mentorUserId(a.getMentorUserId())
                .studentUserId(a.getStudentUserId())
                .coordinatorUserId(a.getCoordinatorUserId())
                .allocationType(a.getAllocationType())
                .status(a.getStatus())
                .allocatedDate(a.getAllocatedDate())
                .deactivatedDate(a.getDeactivatedDate())
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
