package com.bruceyulin.claimpilot.service;

import com.bruceyulin.claimpilot.dto.AuditTrailDTO;
import com.bruceyulin.claimpilot.mapper.AuditTrailMapper;
import com.bruceyulin.claimpilot.model.AuditTrail;
import com.bruceyulin.claimpilot.model.Claim;
import com.bruceyulin.claimpilot.model.User;
import com.bruceyulin.claimpilot.repository.AuditTrailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditTrailService {

  private final AuditTrailRepository auditTrailRepository;

  public List<AuditTrailDTO> getAuditTrailsForClaim(Long claimId) {
    return auditTrailRepository.findByClaimIdOrderByTimestampDesc(claimId)
        .stream()
        .map(AuditTrailMapper::toDTO)
        .toList();
  }

  @Transactional
  public void logAction(User user, Claim claim, String action, String details) {
    AuditTrail log = new AuditTrail();
    log.setUser(user);
    log.setClaim(claim);
    log.setAction(action);
    log.setDetails(details);
    auditTrailRepository.save(log);
  }
}