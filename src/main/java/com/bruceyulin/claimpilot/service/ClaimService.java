package com.bruceyulin.claimpilot.service;

import com.bruceyulin.claimpilot.model.Claim;
import com.bruceyulin.claimpilot.repository.ClaimRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public List<Claim> getClaimsByPolicyHolderId(Long policyHolderId) {
        return claimRepository.findByPolicyHolderId(policyHolderId);
    }

    public List<Claim> getClaimsByStatus(String status) {
        return claimRepository.findByStatus(status);
    }

    public List<Claim> getClaimsByClaimType(String claimType) {
        return claimRepository.findByClaimType(claimType);
    }

    public List<Claim> getClaimsByDateRange(LocalDate start, LocalDate end) {
        return claimRepository.findByClaimDateBetween(start, end);
    }

    public List<Claim> getClaimsByPolicyHolderIdAndStatus(Long policyHolderId, String status) {
        return claimRepository.findByPolicyHolderIdAndStatus(policyHolderId, status);
    }

    public Claim createClaim(Claim claim) {
        return claimRepository.save(claim);
    }

    public Claim getClaimById(Long id) {
        return claimRepository.findById(id).orElse(null);
    }

    public void deleteClaim(Long id) {
        claimRepository.deleteById(id);
    }
}