package com.bruceyulin.claimpilot.mapper;

import com.bruceyulin.claimpilot.dto.ClaimDTO;
import com.bruceyulin.claimpilot.model.Claim;

public class ClaimMapper {
    public static Claim toEntity(ClaimDTO dto) {
        if (dto == null)
            return null;

        return Claim.builder()
                .claimType(dto.getClaimType())
                .dateOfAccident(dto.getDateOfAccident())
                .accidentDescription(dto.getAccidentDescription())
                .policeReportNumber(dto.getPoliceReportNumber())
                .locationOfAccident(dto.getLocationOfAccident())
                .damageDescription(dto.getDamageDescription())
                .estimatedRepairCost(dto.getEstimatedRepairCost())
                .finalSettlementAmount(dto.getFinalSettlementAmount())
                .policyHolder(PolicyHolderMapper.toEntity(dto.getPolicyHolder()))
                .build();
    }

    public static ClaimDTO toDTO(Claim claim) {
        if (claim == null)
            return null;

        return ClaimDTO.builder()
                .claimType(claim.getClaimType())
                .dateOfAccident(claim.getDateOfAccident())
                .accidentDescription(claim.getAccidentDescription())
                .policeReportNumber(claim.getPoliceReportNumber())
                .locationOfAccident(claim.getLocationOfAccident())
                .damageDescription(claim.getDamageDescription())
                .estimatedRepairCost(claim.getEstimatedRepairCost())
                .finalSettlementAmount(claim.getFinalSettlementAmount())
                .policyHolder(PolicyHolderMapper.toDTO(claim.getPolicyHolder()))
                .build();
    }
}
