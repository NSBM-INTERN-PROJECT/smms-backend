package com.smms.allocation.repository;

import com.smms.allocation.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, Long> {
    
    // Student ge ID eken allocations hoyanna
    List<Allocation> findByStudentId(String studentId);

    // Mentor ge ID eken allocations hoyanna
    List<Allocation> findByMentorId(String mentorId);
}