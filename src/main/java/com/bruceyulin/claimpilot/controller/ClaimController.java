package com.bruceyulin.claimpilot.controller;

import com.bruceyulin.claimpilot.model.Claim;
import com.bruceyulin.claimpilot.service.ClaimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public List<Claim> getAllClaims() {
        return claimService.getAllClaims();
    }

    @GetMapping("/policyholder/{id}")
    public List<Claim> getClaimsByPolicyHolder(@PathVariable Long id) {
        return claimService.getClaimsByPolicyHolderId(id);
    }

    @GetMapping("/status/{status}")
    public List<Claim> getClaimsByStatus(@PathVariable String status) {
        return claimService.getClaimsByStatus(status);
    }

    @GetMapping("/type/{claimType}")
    public List<Claim> getClaimsByType(@PathVariable String claimType) {
        return claimService.getClaimsByClaimType(claimType);
    }

    @GetMapping("/date")
    public List<Claim> getClaimsByDateRange(
            @RequestParam String start,
            @RequestParam String end) {
        return claimService.getClaimsByDateRange(LocalDate.parse(start), LocalDate.parse(end));
    }

    @GetMapping("/filter")
    public List<Claim> getClaimsByPolicyHolderAndStatus(
            @RequestParam Long policyHolderId,
            @RequestParam String status) {
        return claimService.getClaimsByPolicyHolderIdAndStatus(policyHolderId, status);
    }

    @PostMapping
    public Claim createClaim(@RequestBody Claim claim) {
        return claimService.createClaim(claim);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Claim> getClaimById(@PathVariable Long id) {
        Claim claim = claimService.getClaimById(id);
        if (claim != null) {
            return ResponseEntity.ok(claim);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {
        claimService.deleteClaim(id);
        return ResponseEntity.noContent().build();
    }
}