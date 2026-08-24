package com.smms.allocation.exception;

import org.springframework.http.HttpStatus;

public class NoUnallocatedStudentsException extends AllocationException {
    public NoUnallocatedStudentsException() {
        super("NO_UNALLOCATED_STUDENTS",
              "There are no unallocated students to assign.",
              HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
