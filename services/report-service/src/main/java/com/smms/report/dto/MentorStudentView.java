package com.smms.report.dto;

import com.smms.report.client.dto.StudentProgressSummaryDto;
import com.smms.report.client.dto.StudentSummaryDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * One mentor with their full list of allocated students and progress summaries.
 * Used by the coordinator for the advanced filtering view (FR-005).
 */
@Data @Builder
public class MentorStudentView {
    private Long mentorUserId;
    private String mentorName;
    private String department;
    private String specialization;
    private Integer capacity;          // maxStudents setting
    private int currentStudentCount;
    private List<StudentSummaryDto> students;
    private List<StudentProgressSummaryDto> progressSummaries;
}
