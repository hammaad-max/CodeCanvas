package com.hammaad.codecanvas.repository;

import com.hammaad.codecanvas.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    long countByStudentIdAndCreatedAtAfter(Long studentId, Instant since);

    /** Invalidate every outstanding link for a student (after a successful reset). */
    @Modifying
    @Query("update PasswordResetToken t set t.usedAt = :now where t.studentId = :studentId and t.usedAt is null")
    int invalidateAll(@Param("studentId") Long studentId, @Param("now") Instant now);
}
