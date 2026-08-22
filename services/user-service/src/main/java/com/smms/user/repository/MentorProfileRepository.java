package com.smms.user.repository;

import com.smms.user.domain.MentorProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {
    Optional<MentorProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    boolean existsByEmployeeId(String employeeId);
    Page<MentorProfile> findByIsActiveTrueAndDepartmentContainingIgnoreCase(String department, Pageable pageable);
    Page<MentorProfile> findByIsActiveTrue(Pageable pageable);
    List<MentorProfile> findByIsActiveTrue();
}
