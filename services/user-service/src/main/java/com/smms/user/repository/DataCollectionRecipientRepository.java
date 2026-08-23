package com.smms.user.repository;

import com.smms.user.entity.DataCollectionRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataCollectionRecipientRepository extends JpaRepository<DataCollectionRecipient, Long> {

    List<DataCollectionRecipient> findByRequestId(Long requestId);
}