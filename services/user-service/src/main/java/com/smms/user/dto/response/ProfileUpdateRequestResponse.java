package com.smms.user.dto.response;

import com.smms.user.entity.ProfileUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequestResponse {

    private Long id;
    private Long studentUserId;
    private Long mentorUserId;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private ProfileUpdateRequest.RequestStatus status;
    private String mentorNotes;
    private Instant createdAt;
    private Instant reviewedAt;
}