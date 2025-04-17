package com.bruceyulin.claimpilot.repository;

import com.bruceyulin.claimpilot.model.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {

  // View all audit logs for a specific claim (already exists)
  List<AuditTrail> findByClaimIdOrderByTimestampDesc(Long claimId);

  // View all actions taken by a specific adjuster
  List<AuditTrail> findByUserIdOrderByTimestampDesc(Long userId);

  // View all logs for a claim, performed by a specific adjuster
  List<AuditTrail> findByClaimIdAndUserIdOrderByTimestampDesc(Long claimId, Long userId);
}