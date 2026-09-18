package com.smms.user.dto.response;

import com.smms.user.domain.ProfileUpdateRequest;
import com.smms.user.domain.RequestStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class ProfileUpdateRequestResponse {
    private Long id;
    private Long studentUserId;
    private Long mentorUserId;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private RequestStatus status;
    private String mentorNotes;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    public static ProfileUpdateRequestResponse from(ProfileUpdateRequest r) {
        return ProfileUpdateRequestResponse.builder()
                .id(r.getId()).studentUserId(r.getStudentUserId())
                .mentorUserId(r.getMentorUserId()).fieldName(r.getFieldName())
                .oldValue(r.getOldValue()).newValue(r.getNewValue())
                .status(r.getStatus()).mentorNotes(r.getMentorNotes())
                .reviewedAt(r.getReviewedAt()).createdAt(r.getCreatedAt()).build();
    }
}
