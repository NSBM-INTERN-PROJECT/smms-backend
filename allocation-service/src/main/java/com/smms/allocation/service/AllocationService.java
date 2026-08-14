package com.smms.allocation.service;

import com.smms.allocation.dto.request.AllocationRequest;
import com.smms.allocation.dto.response.AllocationResponse;

import java.util.List;

public interface AllocationService {
    
    AllocationResponse allocateMentor(AllocationRequest request);
    
    List<AllocationResponse> getAllAllocations();
    
    List<AllocationResponse> getAllocationsByStudent(String studentId);
    
    List<AllocationResponse> getAllocationsByMentor(String mentorId);
}