package com.smms.session.repository;

import com.smms.session.domain.Escalation;
import com.smms.session.domain.EscalationCategory;
import com.smms.session.domain.EscalationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EscalationRepository extends JpaRepository<Escalation, Long> {

    Page<Escalation> findByStudentUserIdOrderByCreatedAtDesc(Long studentUserId, Pageable pageable);
    Page<Escalation> findByMentorUserIdOrderByCreatedAtDesc(Long mentorUserId, Pageable pageable);

    @Query("""SELECT e FROM Escalation e WHERE
           (:status IS NULL OR e.status = :status) AND
           (:category IS NULL OR e.category = :category)
           ORDER BY e.createdAt DESC""")
    Page<Escalation> findByFilters(
            @Param("status") EscalationStatus status,
            @Param("category") EscalationCategory category,
            Pageable pageable);

    List<Escalation> findByStudentUserIdAndStatusIn(Long studentUserId, List<EscalationStatus> statuses);
    long countByStudentUserIdAndStatus(Long studentUserId, EscalationStatus status);
    long countByStatus(EscalationStatus status);
}
