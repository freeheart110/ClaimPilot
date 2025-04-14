package com.bruceyulin.claimpilot.controller;

import com.bruceyulin.claimpilot.dto.ClaimDTO;
import com.bruceyulin.claimpilot.dto.AssignAdjusterRequest;

import com.bruceyulin.claimpilot.mapper.ClaimMapper;
import com.bruceyulin.claimpilot.model.Claim;
import com.bruceyulin.claimpilot.service.ClaimService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.bruceyulin.claimpilot.model.User;
import com.bruceyulin.claimpilot.repository.UserRepository;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = "*") // allow requests from frontend dev server
public class ClaimController {

    private final ClaimService claimService;
    private final UserRepository userRepository;

    public ClaimController(ClaimService claimService, UserRepository userRepository) {
        this.claimService = claimService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<ClaimDTO> getAllClaims() {
        List<Claim> claims = claimService.getAllClaims();
        return claims.stream()
                .map(ClaimMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADJUSTER')")
    @GetMapping("/assigned")
    public List<ClaimDTO> getAssignedClaims(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return claimService.getClaimsForAdjuster(user.getId());
    }

    @GetMapping("/status")
    public ResponseEntity<String> getClaimStatusByFlexibleQuery(
            @RequestParam(required = false) String claimNumber,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {
        try {
            String status = claimService.getClaimStatusFlexible(claimNumber, email, firstName, lastName);
            return ResponseEntity.ok(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimDTO> getClaimById(@PathVariable Long id) {
        Claim claim = claimService.getClaimById(id);
        if (claim != null) {
            return ResponseEntity.ok(ClaimMapper.toDTO(claim));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {
        claimService.deleteClaim(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ClaimDTO> submitClaim(@RequestBody ClaimDTO claimDTO) {
        Claim claim = claimService.submitClaim(claimDTO); // service returns a Claim
        ClaimDTO responseDTO = ClaimMapper.toDTO(claim); // map entity → DTO
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaimDTO> updateClaim(@PathVariable Long id, @RequestBody ClaimDTO updatedClaimDto) {
        Claim updatedClaim = claimService.updateClaim(id, updatedClaimDto);
        return ResponseEntity.ok(ClaimMapper.toDTO(updatedClaim));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<ClaimDTO> assignAdjusterToClaim(
            @PathVariable Long id,
            @RequestBody AssignAdjusterRequest request) {
        Claim updatedClaim = claimService.assignAdjuster(id, request.getAdjusterId());
        return ResponseEntity.ok(ClaimMapper.toDTO(updatedClaim));
    }
}