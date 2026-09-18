package com.smms.user.dto.response;

import com.smms.user.domain.DataCollectionRequest;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class DataCollectionResponse {
    private Long id;
    private Long mentorUserId;
    private String filterCriteria;
    private String message;
    private int totalRecipients;
    private LocalDateTime createdAt;

    public static DataCollectionResponse from(DataCollectionRequest r, int total) {
        return DataCollectionResponse.builder()
                .id(r.getId()).mentorUserId(r.getMentorUserId())
                .filterCriteria(r.getFilterCriteria()).message(r.getMessage())
                .totalRecipients(total).createdAt(r.getCreatedAt()).build();
    }
}
