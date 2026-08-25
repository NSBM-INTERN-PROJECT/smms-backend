package com.smms.allocation.repository;

import com.smms.allocation.domain.Allocation;
import com.smms.allocation.domain.AllocationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    /** Find a student's current active allocation. */
    Optional<Allocation> findByStudentUserIdAndStatus(Long studentUserId, AllocationStatus status);

    /** All active allocations for a mentor (their student list). */
    List<Allocation> findByMentorUserIdAndStatus(Long mentorUserId, AllocationStatus status);

    /** Paginated list of all allocations (admin view), optionally filtered by status. */
    Page<Allocation> findByStatus(AllocationStatus status, Pageable pageable);

    /** All allocations regardless of status (admin history view). */
    Page<Allocation> findAll(Pageable pageable);

    /** Check if a student already has an active allocation. */
    boolean existsByStudentUserIdAndStatus(Long studentUserId, AllocationStatus status);

    /** Check if a mentor has capacity — count their current ACTIVE students. */
    long countByMentorUserIdAndStatus(Long mentorUserId, AllocationStatus status);

    /** Returns student IDs that already have an ACTIVE allocation. */
    @Query("SELECT a.studentUserId FROM Allocation a WHERE a.status = 'ACTIVE'")
    List<Long> findAllocatedStudentIds();

    /** Count of active students per mentor — for capacity enforcement. */
    @Query("SELECT a.mentorUserId, COUNT(a) FROM Allocation a WHERE a.status = 'ACTIVE' GROUP BY a.mentorUserId")
    List<Object[]> countActiveStudentsPerMentor();
}
