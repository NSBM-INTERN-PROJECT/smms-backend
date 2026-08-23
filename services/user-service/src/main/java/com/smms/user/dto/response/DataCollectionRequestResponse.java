package com.smms.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataCollectionRequestResponse {

    private Long id;
    private Long mentorUserId;
    private String filterCriteria;
    private String message;
    private Instant createdAt;
    private Integer totalRecipients;
    private Integer submittedCount;
    private Integer pendingCount;
}