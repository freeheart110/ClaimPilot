package com.bruceyulin.claimpilot.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimSimpleDTO {
  private Long id;
  private String claimNumber;
}