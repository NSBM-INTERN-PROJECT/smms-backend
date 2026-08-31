package com.smms.user.repository;

import com.smms.user.domain.RiskStatus;
import com.smms.user.domain.StudentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    boolean existsByStudentId(String studentId);

    @Query("""
SELECT s FROM StudentProfile s WHERE s.isActive = true
           AND (:batch IS NULL OR s.batch = :batch)
           AND (:department IS NULL OR s.department = :department)
           AND (:riskStatus IS NULL OR s.riskStatus = :riskStatus)""")
    Page<StudentProfile> findByFilters(
            @Param("batch") String batch,
            @Param("department") String department,
            @Param("riskStatus") RiskStatus riskStatus,
            Pageable pageable);

    List<StudentProfile> findByBatchAndDepartmentAndIsActiveTrue(String batch, String department);
    List<StudentProfile> findByBatchAndIsActiveTrue(String batch);
    List<StudentProfile> findByDepartmentAndIsActiveTrue(String department);
}
