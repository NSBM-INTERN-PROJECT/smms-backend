package com.smms.allocation.service.impl;

import com.smms.allocation.dto.request.AllocationRequest;
import com.smms.allocation.dto.response.AllocationResponse;
import com.smms.allocation.entity.Allocation;
import com.smms.allocation.repository.AllocationRepository;
import com.smms.allocation.service.AllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllocationServiceImpl implements AllocationService {

    private final AllocationRepository allocationRepository;

    @Override
    public AllocationResponse allocateMentor(AllocationRequest request) {
        // Request eken Entity ekata data map kireema
        Allocation allocation = Allocation.builder()
                .studentId(request.getStudentId())
                .mentorId(request.getMentorId())
                .academicYear(request.getAcademicYear())
                .status("ACTIVE") // Default status eka ACTIVE kiyala danawa
                .build();

        // Database eke save kireema
        Allocation savedAllocation = allocationRepository.save(allocation);

        // Save unu data tika Response ekak widiyata return kireema
        return mapToResponse(savedAllocation);
    }

    @Override
    public List<AllocationResponse> getAllAllocations() {
        return allocationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AllocationResponse> getAllocationsByStudent(String studentId) {
        return allocationRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AllocationResponse> getAllocationsByMentor(String mentorId) {
        return allocationRepository.findByMentorId(mentorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Entity eka Response DTO ekata convert karana podi method ekak (Code eka repeat wenna nathi wenna)
    private AllocationResponse mapToResponse(Allocation allocation) {
        return AllocationResponse.builder()
                .id(allocation.getId())
                .studentId(allocation.getStudentId())
                .mentorId(allocation.getMentorId())
                .academicYear(allocation.getAcademicYear())
                .status(allocation.getStatus())
                .createdAt(allocation.getCreatedAt())
                .build();
    }
}