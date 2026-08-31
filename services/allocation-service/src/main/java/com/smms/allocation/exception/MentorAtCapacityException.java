package com.smms.allocation.exception;

import org.springframework.http.HttpStatus;

public class MentorAtCapacityException extends AllocationException {
    public MentorAtCapacityException(Long mentorUserId, int max) {
        super("MENTOR_AT_CAPACITY",
              "Mentor " + mentorUserId + " has reached their maximum student capacity of " + max + ".",
              HttpStatus.CONFLICT);
    }
}
