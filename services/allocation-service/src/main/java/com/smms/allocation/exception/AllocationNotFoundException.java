package com.smms.allocation.exception;

import org.springframework.http.HttpStatus;

public class AllocationNotFoundException extends AllocationException {
    public AllocationNotFoundException(Long id) {
        super("ALLOCATION_NOT_FOUND", "Allocation not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
