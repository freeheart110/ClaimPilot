package com.bruceyulin.claimpilot.dto;

import com.bruceyulin.claimpilot.model.ClaimType;
import com.bruceyulin.claimpilot.model.ClaimStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDTO {
    private Long id;
    private String claimNumber;
    private ClaimType claimType;
    private ClaimStatus status;
    private LocalDate claimDate;
    private LocalDate dateOfAccident;
    private String accidentDescription;
    private String policeReportNumber;
    private String locationOfAccident;
    private String damageDescription;
    private BigDecimal estimatedRepairCost;
    private BigDecimal finalSettlementAmount;

    private PolicyHolderDTO policyHolder;
}