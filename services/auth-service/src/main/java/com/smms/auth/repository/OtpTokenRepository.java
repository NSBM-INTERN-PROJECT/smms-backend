package com.smms.auth.repository;

import com.smms.auth.domain.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    /**
     * Finds the most recent unused, unexpired OTP for a user.
     */
    Optional<OtpToken> findTopByUserIdAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            Long userId, LocalDateTime now);

    /**
     * Finds the most recent OTP for a user (used or not) — for cooldown check.
     */
    Optional<OtpToken> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Query("DELETE FROM OtpToken o WHERE o.userId = :userId")
    void deleteAllByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM OtpToken o WHERE o.expiresAt < :cutoff")
    void deleteExpiredBefore(LocalDateTime cutoff);
}
