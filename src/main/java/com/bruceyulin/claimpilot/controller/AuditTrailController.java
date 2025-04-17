// package com.bruceyulin.claimpilot.controller;

// import com.bruceyulin.claimpilot.dto.AuditTrailDTO;
// import com.bruceyulin.claimpilot.mapper.AuditTrailMapper;
// import com.bruceyulin.claimpilot.repository.AuditTrailRepository;
// import com.bruceyulin.claimpilot.model.AuditTrail;

// import lombok.RequiredArgsConstructor;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/audit")
// @RequiredArgsConstructor
// public class AuditTrailController {

// private final AuditTrailRepository auditTrailRepository;

// // Only admins can view audit history
// @PreAuthorize("hasRole('ADMIN')")
// @GetMapping("/claim/{claimId}")
// public List<AuditTrailDTO> getAuditByClaim(@PathVariable Long claimId) {
// List<AuditTrail> audits =
// auditTrailRepository.findByClaimIdOrderByTimestampDesc(claimId);
// return audits.stream().map(AuditTrailMapper::toDTO).toList();
// }
// }