package com.smms.report.dto;

import lombok.Data;

/**
 * FR-005: Advanced mentor filtering — used for the coordinator's
 * "view all mentors and their students" endpoint.
 */
@Data
public class MentorFilterRequest {
    private String department;
    private String specialization;
    private Integer minStudents;   // min currently-allocated student count
    private Integer maxStudents;   // max currently-allocated student count
    private String progressStatus; // filter mentors who have students with this status
    private String batch;          // filter by student batch
}
