package com.bruceyulin.claimpilot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser(Authentication auth) {
    if (auth == null || !auth.isAuthenticated()) {
      return ResponseEntity.status(401).body("Not authenticated");
    }

    org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) auth
        .getPrincipal();

    String email = principal.getUsername();
    String role = principal.getAuthorities().stream()
        .findFirst()
        .map(granted -> granted.getAuthority().replace("ROLE_", ""))
        .orElse("UNKNOWN");

    return ResponseEntity.ok(Map.of(
        "email", email,
        "role", role));
  }
}