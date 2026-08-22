package com.smms.user.repository;

import com.smms.user.domain.DataCollectionRecipient;
import com.smms.user.domain.RecipientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DataCollectionRecipientRepository extends JpaRepository<DataCollectionRecipient, Long> {
    List<DataCollectionRecipient> findByRequestId(Long requestId);
    Optional<DataCollectionRecipient> findByRequestIdAndStudentUserId(Long requestId, Long studentUserId);
    List<DataCollectionRecipient> findByStudentUserIdAndStatus(Long studentUserId, RecipientStatus status);

    @Modifying
    @Query("UPDATE DataCollectionRecipient r SET r.status = 'SUBMITTED', r.respondedAt = CURRENT_TIMESTAMP WHERE r.requestId = :requestId AND r.studentUserId = :studentUserId")
    int markAsSubmitted(Long requestId, Long studentUserId);
}
