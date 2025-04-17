package com.bruceyulin.claimpilot.mapper;

import com.bruceyulin.claimpilot.dto.AuditTrailDTO;
import com.bruceyulin.claimpilot.dto.ClaimSimpleDTO;
import com.bruceyulin.claimpilot.model.AuditTrail;

public class AuditTrailMapper {
  public static AuditTrailDTO toDTO(AuditTrail audit) {
    if (audit == null)
      return null;

    return AuditTrailDTO.builder()
        .id(audit.getId())
        .action(audit.getAction())
        .details(audit.getDetails())
        .timestamp(audit.getTimestamp())
        .user(UserMapper.toDTO(audit.getUser()))
        .claim(ClaimSimpleDTO.builder()
            .id(audit.getClaim().getId())
            .claimNumber(audit.getClaim().getClaimNumber())
            .build())
        .build();
  }
}