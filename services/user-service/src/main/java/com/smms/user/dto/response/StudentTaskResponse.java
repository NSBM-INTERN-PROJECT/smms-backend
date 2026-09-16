package com.smms.user.dto.response;

import com.smms.user.domain.RecipientStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTaskResponse {
    private Long id;              // request ID
    private Long recipientId;     // recipient record ID
    private Long mentorUserId;
    private String creatorMentorName;
    private String title;
    private String description;
    private String batch;
    private String department;
    private RecipientStatus status; // PENDING or SUBMITTED
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
