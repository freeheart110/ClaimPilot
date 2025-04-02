package com.bruceyulin.claimpilot.repository;

import com.bruceyulin.claimpilot.model.Claim;
import com.bruceyulin.claimpilot.model.PolicyHolder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByPolicyHolderId(Long policyHolderId);

    List<Claim> findByStatus(String status);

    List<Claim> findByClaimType(String claimType);

    List<Claim> findByClaimDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT c FROM Claim c WHERE c.policyHolder.id = :policyHolderId AND c.status = :status")
    List<Claim> findByPolicyHolderIdAndStatus(@Param("policyHolderId") Long policyHolderId,
            @Param("status") String status);

    Optional<Claim> findByClaimNumberAndPolicyHolder(String claimNumber, PolicyHolder policyHolder);
}
