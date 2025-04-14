package com.bruceyulin.claimpilot.service;

import com.bruceyulin.claimpilot.model.Claim;
import com.bruceyulin.claimpilot.model.ClaimStatus;
import com.bruceyulin.claimpilot.model.PolicyHolder;
import com.bruceyulin.claimpilot.repository.ClaimRepository;
import com.bruceyulin.claimpilot.repository.UserRepository;
import com.bruceyulin.claimpilot.model.User;
import com.bruceyulin.claimpilot.model.Role;

import jakarta.persistence.EntityNotFoundException;

import com.bruceyulin.claimpilot.dto.ClaimDTO;
import com.bruceyulin.claimpilot.dto.PolicyHolderDTO;
import com.bruceyulin.claimpilot.mapper.ClaimMapper;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyHolderService policyHolderService;
    private final UserRepository userRepository;

    public ClaimService(ClaimRepository claimRepository, PolicyHolderService policyHolderService,
            UserRepository userRepository) {
        this.claimRepository = claimRepository;
        this.policyHolderService = policyHolderService;
        this.userRepository = userRepository;
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

    public Claim submitClaim(ClaimDTO claimDTO) {
        // Find or create the policyholder
        PolicyHolderDTO phDto = claimDTO.getPolicyHolder();

        PolicyHolder policyHolder = policyHolderService.findOrCreate(
                phDto.getFirstName(),
                phDto.getLastName(),
                phDto.getEmail(),
                phDto.getPhone(),
                phDto.getAddress(),
                phDto.getCity(),
                phDto.getProvince(),
                phDto.getPostalCode(),
                phDto.getDriverLicenseNumber(),
                phDto.getVehicleVIN());

        // Convert ClaimDTO to Claim entity using mapper
        Claim claim = ClaimMapper.toEntity(claimDTO);
        claim.setClaimNumber(generateClaimNumber());
        claim.setClaimType(claimDTO.getClaimType());
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setClaimDate(LocalDate.now());
        claim.setDateOfAccident(claimDTO.getDateOfAccident());
        claim.setAccidentDescription(claimDTO.getAccidentDescription());
        claim.setPoliceReportNumber(claimDTO.getPoliceReportNumber());
        claim.setLocationOfAccident(claimDTO.getLocationOfAccident());
        claim.setDamageDescription(claimDTO.getDamageDescription());
        claim.setEstimatedRepairCost(claimDTO.getEstimatedRepairCost());
        claim.setFinalSettlementAmount(claimDTO.getFinalSettlementAmount());
        claim.setPolicyHolder(policyHolder);
        // Automatically assign the adjuster with the least workload
        List<Long> adjusterIds = userRepository.findAdjustersByLeastClaims();
        if (!adjusterIds.isEmpty()) {
            Random random = new Random();
            Long chosenAdjusterId = adjusterIds.get(random.nextInt(adjusterIds.size()));
            User adjuster = userRepository.findById(chosenAdjusterId)
                    .orElseThrow(() -> new RuntimeException("Adjuster not found"));
            claim.setAssignedAdjuster(adjuster);
        }
        return claimRepository.save(claim);
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

        // Update claim fields (conditionally)
        if (updatedClaimDto.getStatus() != null) {
            claim.setStatus(updatedClaimDto.getStatus());
        }
        if (updatedClaimDto.getEstimatedRepairCost() != null) {
            claim.setEstimatedRepairCost(updatedClaimDto.getEstimatedRepairCost());
        }
        if (updatedClaimDto.getFinalSettlementAmount() != null) {
            claim.setFinalSettlementAmount(updatedClaimDto.getFinalSettlementAmount());
        }
        if (updatedClaimDto.getDateOfAccident() != null) {
            claim.setDateOfAccident(updatedClaimDto.getDateOfAccident());
        }
        if (updatedClaimDto.getAccidentDescription() != null) {
            claim.setAccidentDescription(updatedClaimDto.getAccidentDescription());
        }
        if (updatedClaimDto.getPoliceReportNumber() != null) {
            claim.setPoliceReportNumber(updatedClaimDto.getPoliceReportNumber());
        }
        if (updatedClaimDto.getLocationOfAccident() != null) {
            claim.setLocationOfAccident(updatedClaimDto.getLocationOfAccident());
        }
        if (updatedClaimDto.getDamageDescription() != null) {
            claim.setDamageDescription(updatedClaimDto.getDamageDescription());
        }

        // Update policyholder (if included in the DTO)
        if (updatedClaimDto.getPolicyHolder() != null) {
            PolicyHolder policyHolder = claim.getPolicyHolder();
            PolicyHolderDTO ph = updatedClaimDto.getPolicyHolder();

            if (ph.getFirstName() != null)
                policyHolder.setFirstName(ph.getFirstName());
            if (ph.getLastName() != null)
                policyHolder.setLastName(ph.getLastName());
            if (ph.getEmail() != null)
                policyHolder.setEmail(ph.getEmail());
            if (ph.getPhone() != null)
                policyHolder.setPhone(ph.getPhone());
            if (ph.getAddress() != null)
                policyHolder.setAddress(ph.getAddress());
            if (ph.getCity() != null)
                policyHolder.setCity(ph.getCity());
            if (ph.getProvince() != null)
                policyHolder.setProvince(ph.getProvince());
            if (ph.getPostalCode() != null)
                policyHolder.setPostalCode(ph.getPostalCode());
            if (ph.getDriverLicenseNumber() != null)
                policyHolder.setDriverLicenseNumber(ph.getDriverLicenseNumber());
            if (ph.getVehicleVIN() != null)
                policyHolder.setVehicleVIN(ph.getVehicleVIN());

            claim.setPolicyHolder(policyHolder);
        }

        return claimRepository.save(claim);
    }

    private String generateClaimNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomPart = (int) (Math.random() * 1000000);
        String formattedRandom = String.format("%06d", randomPart);
        return "CLM-" + datePart + "-" + formattedRandom;
    }

    public Claim assignAdjuster(Long claimId, Long adjusterId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new EntityNotFoundException("Claim not found"));

        User adjuster = userRepository.findById(adjusterId)
                .orElseThrow(() -> new EntityNotFoundException("Adjuster not found"));

        if (adjuster.getRole() != Role.ADJUSTER) {
            throw new IllegalArgumentException("User is not an adjuster");
        }

        claim.setAssignedAdjuster(adjuster);
        return claimRepository.save(claim);
    }

    public List<ClaimDTO> getClaimsForAdjuster(Long adjusterId) {
        List<Claim> claims = claimRepository.findClaimsByAssignedAdjusterId(adjusterId);
        return claims.stream()
                .map(ClaimMapper::toDTO)
                .toList();
    }
}