package com.smms.report.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Top-level KPI block for the admin/coordinator dashboard.
 * Aggregated across all mentors/students/meetings.
 */
@Data @Builder
public class DashboardStats {
    // Allocation
    private long totalStudents;
    private long allocatedStudents;
    private long unallocatedStudents;
    private long totalMentors;
    private long totalAllocations;

    // Meetings
    private long totalMeetings;
    private long completedMeetings;
    private long scheduledMeetings;
    private long cancelledMeetings;

    // Attendance
    private long presentCount;
    private long absentCount;
    private long lateCount;

    // Progress
    private long studentsOnTrack;
    private long studentsNeedsAttention;
    private long studentsAtRisk;
    private long studentsCritical;

    // Escalations
    private long openEscalations;
    private long resolvedEscalations;
    private long totalEscalations;
}
