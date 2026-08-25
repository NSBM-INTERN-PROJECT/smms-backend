package com.smms.report.dto;

import com.smms.report.client.dto.StudentProgressSummaryDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * Mentor-specific dashboard: their own student list with progress summaries.
 */
@Data @Builder
public class MentorDashboard {
    private Long mentorUserId;
    private int totalStudents;
    private int studentsOnTrack;
    private int studentsNeedsAttention;
    private int studentsAtRisk;
    private int studentsCritical;
    private long totalMeetings;
    private long completedMeetings;
    private long openEscalations;
    private List<StudentProgressSummaryDto> studentSummaries;
}
