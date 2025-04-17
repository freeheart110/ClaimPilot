package com.bruceyulin.claimpilot.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditTrailDTO {
  private Long id;
  private String action;
  private String details;
  private LocalDateTime timestamp;
  private UserDTO user;
  private ClaimSimpleDTO claim;
}