package com.smms.report.client.dto;

import lombok.Data;
import java.util.List;

/** Generic wrapper matching the PagedResponse<T> shape returned by downstream services. */
@Data
public class PagedResponseDto<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
