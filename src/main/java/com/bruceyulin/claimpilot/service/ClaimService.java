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

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.text.DecimalFormat;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyHolderService policyHolderService;
    private final UserRepository userRepository;
    private final AuditTrailService auditTrailService;

    public ClaimService(ClaimRepository claimRepository, PolicyHolderService policyHolderService,
            UserRepository userRepository, AuditTrailService auditTrailService) {
        this.claimRepository = claimRepository;
        this.policyHolderService = policyHolderService;
        this.userRepository = userRepository;
        this.auditTrailService = auditTrailService;
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
        System.out.println("Incoming estimatedRepairCost: " + claimDTO.getEstimatedRepairCost());
        System.out.println("Incoming finalSettlementAmount: " + claimDTO.getFinalSettlementAmount());
        // Automatically assign the adjuster with the least workload
        List<Long> adjusterIds = userRepository.findAdjustersByLeastClaims();
        User adjuster = null;
        if (!adjusterIds.isEmpty()) {
            Random random = new Random();
            Long chosenAdjusterId = adjusterIds.get(random.nextInt(adjusterIds.size()));
            adjuster = userRepository.findById(chosenAdjusterId)
                    .orElseThrow(() -> new RuntimeException("Adjuster not found"));
            claim.setAssignedAdjuster(adjuster);
        }

        // Save the claim first before logging audit trail
        Claim savedClaim = claimRepository.save(claim);

        // Log adjuster assignment if applicable
        if (adjuster != null) {
            auditTrailService.logAction(
                    adjuster,
                    savedClaim,
                    "Adjuster Assigned",
                    "Claim auto-assigned to adjuster: " + adjuster.getFirstName() + " " + adjuster.getLastName());
        }

        // Log claim submission
        auditTrailService.logAction(
                adjuster, // or use a system/admin user if preferred
                savedClaim,
                "Claim Submitted",
                "New claim submitted with claim number: " + savedClaim.getClaimNumber());

        return savedClaim;
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

        StringBuilder details = new StringBuilder("Updated fields:");
        DecimalFormat df = new DecimalFormat("0.00");

        // Compare status
        if (updatedClaimDto.getStatus() != null && updatedClaimDto.getStatus() != claim.getStatus()) {
            details.append(" status from ").append(claim.getStatus())
                    .append(" to ").append(updatedClaimDto.getStatus()).append(";");
            claim.setStatus(updatedClaimDto.getStatus());
        }

        // Compare estimatedRepairCost
        if (updatedClaimDto.getEstimatedRepairCost() != null &&
                (claim.getEstimatedRepairCost() == null ||
                        updatedClaimDto.getEstimatedRepairCost().compareTo(claim.getEstimatedRepairCost()) != 0)) {

            details.append(" estimatedRepairCost from ")
                    .append(claim.getEstimatedRepairCost() != null ? df.format(claim.getEstimatedRepairCost()) : "null")
                    .append(" to ").append(df.format(updatedClaimDto.getEstimatedRepairCost()))
                    .append(";");
            claim.setEstimatedRepairCost(updatedClaimDto.getEstimatedRepairCost());
        }

        // Compare finalSettlementAmount
        if (updatedClaimDto.getFinalSettlementAmount() != null &&
                (claim.getFinalSettlementAmount() == null ||
                        updatedClaimDto.getFinalSettlementAmount().compareTo(claim.getFinalSettlementAmount()) != 0)) {

            details.append(" finalSettlementAmount from ")
                    .append(claim.getFinalSettlementAmount() != null
                            ? df.format(claim.getFinalSettlementAmount())
                            : "null")
                    .append(" to ")
                    .append(df.format(updatedClaimDto.getFinalSettlementAmount()))
                    .append(";");

            claim.setFinalSettlementAmount(updatedClaimDto.getFinalSettlementAmount());
        }

        // Compare dateOfAccident
        if (updatedClaimDto.getDateOfAccident() != null &&
                !updatedClaimDto.getDateOfAccident().equals(claim.getDateOfAccident())) {
            details.append(" dateOfAccident from ").append(claim.getDateOfAccident())
                    .append(" to ").append(updatedClaimDto.getDateOfAccident()).append(";");
            claim.setDateOfAccident(updatedClaimDto.getDateOfAccident());
        }

        // Compare Strings with explicit "from ... to ..." where applicable
        if (updatedClaimDto.getAccidentDescription() != null &&
                !updatedClaimDto.getAccidentDescription().equals(claim.getAccidentDescription())) {
            details.append(" accidentDescription from ").append(claim.getAccidentDescription())
                    .append(" to ").append(updatedClaimDto.getAccidentDescription()).append(";");
            claim.setAccidentDescription(updatedClaimDto.getAccidentDescription());
        }

        if (updatedClaimDto.getPoliceReportNumber() != null &&
                !updatedClaimDto.getPoliceReportNumber().equals(claim.getPoliceReportNumber())) {
            details.append(" policeReportNumber from ").append(claim.getPoliceReportNumber())
                    .append(" to ").append(updatedClaimDto.getPoliceReportNumber()).append(";");
            claim.setPoliceReportNumber(updatedClaimDto.getPoliceReportNumber());
        }

        if (updatedClaimDto.getLocationOfAccident() != null &&
                !updatedClaimDto.getLocationOfAccident().equals(claim.getLocationOfAccident())) {
            details.append(" locationOfAccident from ").append(claim.getLocationOfAccident())
                    .append(" to ").append(updatedClaimDto.getLocationOfAccident()).append(";");
            claim.setLocationOfAccident(updatedClaimDto.getLocationOfAccident());
        }

        if (updatedClaimDto.getDamageDescription() != null &&
                !updatedClaimDto.getDamageDescription().equals(claim.getDamageDescription())) {
            details.append(" damageDescription from ").append(claim.getDamageDescription())
                    .append(" to ").append(updatedClaimDto.getDamageDescription()).append(";");
            claim.setDamageDescription(updatedClaimDto.getDamageDescription());
        }

        // --- Update policyholder ---
        if (updatedClaimDto.getPolicyHolder() != null) {
            PolicyHolder policyHolder = claim.getPolicyHolder();
            PolicyHolderDTO ph = updatedClaimDto.getPolicyHolder();

            if (ph.getFirstName() != null && !ph.getFirstName().equals(policyHolder.getFirstName())) {
                details.append(" policyHolder.firstName from ").append(policyHolder.getFirstName())
                        .append(" to ").append(ph.getFirstName()).append(";");
                policyHolder.setFirstName(ph.getFirstName());
            }
            if (ph.getLastName() != null && !ph.getLastName().equals(policyHolder.getLastName())) {
                details.append(" policyHolder.lastName from ").append(policyHolder.getLastName())
                        .append(" to ").append(ph.getLastName()).append(";");
                policyHolder.setLastName(ph.getLastName());
            }
            if (ph.getEmail() != null && !ph.getEmail().equals(policyHolder.getEmail())) {
                details.append(" policyHolder.email from ").append(policyHolder.getEmail())
                        .append(" to ").append(ph.getEmail()).append(";");
                policyHolder.setEmail(ph.getEmail());
            }
            if (ph.getPhone() != null && !ph.getPhone().equals(policyHolder.getPhone())) {
                details.append(" policyHolder.phone from ").append(policyHolder.getPhone())
                        .append(" to ").append(ph.getPhone()).append(";");
                policyHolder.setPhone(ph.getPhone());
            }
            if (ph.getAddress() != null && !ph.getAddress().equals(policyHolder.getAddress())) {
                details.append(" policyHolder.address from ").append(policyHolder.getAddress())
                        .append(" to ").append(ph.getAddress()).append(";");
                policyHolder.setAddress(ph.getAddress());
            }
            if (ph.getCity() != null && !ph.getCity().equals(policyHolder.getCity())) {
                details.append(" policyHolder.city from ").append(policyHolder.getCity())
                        .append(" to ").append(ph.getCity()).append(";");
                policyHolder.setCity(ph.getCity());
            }
            if (ph.getProvince() != null && !ph.getProvince().equals(policyHolder.getProvince())) {
                details.append(" policyHolder.province from ").append(policyHolder.getProvince())
                        .append(" to ").append(ph.getProvince()).append(";");
                policyHolder.setProvince(ph.getProvince());
            }
            if (ph.getPostalCode() != null && !ph.getPostalCode().equals(policyHolder.getPostalCode())) {
                details.append(" policyHolder.postalCode from ").append(policyHolder.getPostalCode())
                        .append(" to ").append(ph.getPostalCode()).append(";");
                policyHolder.setPostalCode(ph.getPostalCode());
            }
            if (ph.getDriverLicenseNumber() != null &&
                    !ph.getDriverLicenseNumber().equals(policyHolder.getDriverLicenseNumber())) {
                details.append(" policyHolder.driverLicenseNumber from ").append(policyHolder.getDriverLicenseNumber())
                        .append(" to ").append(ph.getDriverLicenseNumber()).append(";");
                policyHolder.setDriverLicenseNumber(ph.getDriverLicenseNumber());
            }
            if (ph.getVehicleVIN() != null && !ph.getVehicleVIN().equals(policyHolder.getVehicleVIN())) {
                details.append(" policyHolder.vehicleVIN from ").append(policyHolder.getVehicleVIN())
                        .append(" to ").append(ph.getVehicleVIN()).append(";");
                policyHolder.setVehicleVIN(ph.getVehicleVIN());
            }

            claim.setPolicyHolder(policyHolder);
        }

        Claim saved = claimRepository.save(claim);

        // Log audit entry if anything changed
        if (!details.toString().equals("Updated fields:")) {
            User currentUser = userRepository.findByEmail(
                    SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));

            auditTrailService.logAction(currentUser, saved, "Updated Claim", details.toString());
        }

        return saved;
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

        User newAdjuster = userRepository.findById(adjusterId)
                .orElseThrow(() -> new EntityNotFoundException("Adjuster not found"));

        if (newAdjuster.getRole() != Role.ADJUSTER) {
            throw new IllegalArgumentException("User is not an adjuster");
        }

        User previousAdjuster = claim.getAssignedAdjuster();

        // Skip if already assigned to the same adjuster
        if (previousAdjuster != null && previousAdjuster.getId().equals(adjusterId)) {
            return claim;
        }

        claim.setAssignedAdjuster(newAdjuster);
        Claim saved = claimRepository.save(claim);

        // Get admin (current user) from security context
        User adminUser = userRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Build audit message
        StringBuilder details = new StringBuilder();
        details.append("Adjuster reassigned by admin ")
                .append(adminUser.getFirstName());

        if (previousAdjuster != null) {
            details.append(". From: ").append(previousAdjuster.getFirstName()).append(" ")
                    .append(previousAdjuster.getLastName());
        } else {
            details.append(". Initial assignment.");
        }

        details.append(" → To: ").append(newAdjuster.getFirstName()).append(" ").append(newAdjuster.getLastName());

        // Log the audit
        auditTrailService.logAction(adminUser, saved, "Adjuster Assigned ", details.toString());

        return saved;
    }

    public List<ClaimDTO> getClaimsForAdjuster(Long adjusterId) {
        List<Claim> claims = claimRepository.findClaimsByAssignedAdjusterId(adjusterId);
        return claims.stream()
                .map(ClaimMapper::toDTO)
                .toList();
    }
}