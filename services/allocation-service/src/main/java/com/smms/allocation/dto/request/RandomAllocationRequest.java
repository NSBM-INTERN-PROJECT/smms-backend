package com.smms.allocation.dto.request;

import lombok.Data;

/**
 * Optional filters for the random allocation run.
 * If no filters are provided, ALL unallocated students are processed.
 */
@Data
public class RandomAllocationRequest {
    /** Only allocate students in this batch (e.g. "2024"). Optional. */
    private String batch;

    /** Only allocate students in this department. Optional. */
    private String department;

    /** If true, skip mentors that are already at max capacity. Default: true. */
    private boolean skipFullMentors = true;
}
