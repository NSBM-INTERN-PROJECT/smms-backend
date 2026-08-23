package com.smms.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataCollectionRequestCreate {

    // Filter criteria — which students to target
    private String department;
    private String degreeProgram;
    private String batch;

    // Optional custom message; default used if blank
    private String message;
}