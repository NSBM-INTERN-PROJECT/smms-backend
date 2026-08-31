package com.smms.allocation.exception;

import org.springframework.http.HttpStatus;

public class NoAvailableMentorsException extends AllocationException {
    public NoAvailableMentorsException() {
        super("NO_AVAILABLE_MENTORS",
              "There are no mentors with available capacity to accept students.",
              HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
