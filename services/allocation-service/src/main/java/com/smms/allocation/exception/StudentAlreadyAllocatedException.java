package com.smms.allocation.exception;

import org.springframework.http.HttpStatus;

public class StudentAlreadyAllocatedException extends AllocationException {
    public StudentAlreadyAllocatedException(Long studentUserId) {
        super("STUDENT_ALREADY_ALLOCATED",
              "Student " + studentUserId + " already has an active mentor allocation.",
              HttpStatus.CONFLICT);
    }
}
