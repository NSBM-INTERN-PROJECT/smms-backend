package com.smms.user.repository;

import com.smms.user.domain.StudentExtendedProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StudentExtendedProfileRepository extends JpaRepository<StudentExtendedProfile, Long> {
    Optional<StudentExtendedProfile> findByStudentUserId(Long studentUserId);
    boolean existsByStudentUserId(Long studentUserId);
}
