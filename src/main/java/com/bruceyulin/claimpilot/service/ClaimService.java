package com.bruceyulin.claimpilot.service;

import com.bruceyulin.claimpilot.model.Claim;
import com.bruceyulin.claimpilot.model.ClaimStatus;
import com.bruceyulin.claimpilot.model.PolicyHolder;
import com.bruceyulin.claimpilot.repository.ClaimRepository;
import com.bruceyulin.claimpilot.dto.ClaimDTO;
import com.bruceyulin.claimpilot.dto.PolicyHolderDTO;
import com.bruceyulin.claimpilot.mapper.ClaimMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Claim getClaimById(Long id) {
        return claimRepository.findById(id).orElse(null);
    }

    public void deleteClaim(Long id) {
        claimRepository.deleteById(id);
    }

    @Autowired
    private ClaimRepository repository;
    @Autowired
    private PolicyHolderService policyHolderService;

    public Claim submitClaim(ClaimDTO claimDTO) {
        // Find or create the policyholder
        PolicyHolderDTO phDto = claimDTO.getPolicyHolder();

        PolicyHolder policyHolder = policyHolderService.findOrCreate(
                phDto.getFirstName(),
                phDto.getLastName(),
                phDto.getEmail(),
                phDto.getPhone());

        // Convert ClaimDTO to Claim entity using mapper
        Claim claim = ClaimMapper.toEntity(claimDTO);
        claim.setClaimNumber(generateClaimNumber());
        claim.setClaimType(claimDTO.getClaimType());
        claim.setStatus(ClaimStatus.SUBMITTED); // ✅ FIX: use enum
        claim.setClaimDate(LocalDate.now());
        claim.setDateOfAccident(claimDTO.getDateOfAccident());
        claim.setAccidentDescription(claimDTO.getAccidentDescription());
        claim.setPoliceReportNumber(claimDTO.getPoliceReportNumber());
        claim.setLocationOfAccident(claimDTO.getLocationOfAccident());
        claim.setDamageDescription(claimDTO.getDamageDescription());
        claim.setEstimatedRepairCost(claimDTO.getEstimatedRepairCost());
        claim.setFinalSettlementAmount(claimDTO.getFinalSettlementAmount());
        claim.setPolicyHolder(policyHolder);

        return repository.save(claim);
    }

    public String getClaimStatusFlexible(String claimNumber, String email, String firstName, String lastName) {
        // Ensure at least two identifiers are present
        int filled = 0;
        if (claimNumber != null && !claimNumber.isBlank())
            filled++;
        if (email != null && !email.isBlank())
            filled++;
        if (firstName != null && !firstName.isBlank() && lastName != null && !lastName.isBlank())
            filled++;

        if (filled < 2) {
            throw new IllegalArgumentException("At least two fields (claim number, email, name) are required.");
        }

        Optional<Claim> result = claimRepository.findAll().stream()
                .filter(claim -> {
                    boolean matches = true;
                    if (claimNumber != null && !claimNumber.isBlank()) {
                        matches = matches && claim.getClaimNumber().equalsIgnoreCase(claimNumber);
                    }
                    if (email != null && !email.isBlank()) {
                        matches = matches && email.equalsIgnoreCase(claim.getPolicyHolder().getEmail());
                    }
                    if (firstName != null && !firstName.isBlank() && lastName != null && !lastName.isBlank()) {
                        matches = matches &&
                                firstName.equalsIgnoreCase(claim.getPolicyHolder().getFirstName()) &&
                                lastName.equalsIgnoreCase(claim.getPolicyHolder().getLastName());
                    }
                    return matches;
                })
                .findFirst();

        return result.map(claim -> claim.getStatus().name())
                .orElseThrow(() -> new RuntimeException("No matching claim found with the provided information."));
    }

    public Claim updateClaim(Long id, ClaimDTO updatedClaimDto) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        // Only update the fields relevant for backoffice
        if (updatedClaimDto.getStatus() != null) {
            claim.setStatus(updatedClaimDto.getStatus());
        }
        if (updatedClaimDto.getEstimatedRepairCost() != null) {
            claim.setEstimatedRepairCost(updatedClaimDto.getEstimatedRepairCost());
        }
        if (updatedClaimDto.getFinalSettlementAmount() != null) {
            claim.setFinalSettlementAmount(updatedClaimDto.getFinalSettlementAmount());
        }

        return claimRepository.save(claim);
    }

    private String generateClaimNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomPart = (int) (Math.random() * 1000000);
        String formattedRandom = String.format("%06d", randomPart);
        return "CLM-" + datePart + "-" + formattedRandom;
    }
}