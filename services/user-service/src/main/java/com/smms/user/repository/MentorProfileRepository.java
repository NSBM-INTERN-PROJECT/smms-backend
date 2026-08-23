package com.smms.user.repository;

import com.smms.user.entity.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {

    Optional<MentorProfile> findByUserId(Long userId);

    boolean existsByEmployeeId(String employeeId);
}
