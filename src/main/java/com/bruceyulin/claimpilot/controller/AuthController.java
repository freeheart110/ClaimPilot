package com.bruceyulin.claimpilot.controller;

import com.bruceyulin.claimpilot.model.User;
import com.bruceyulin.claimpilot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserRepository userRepository; // Inject the repository

  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser(Authentication auth) {
    if (auth == null || !auth.isAuthenticated()) {
      return ResponseEntity.status(401).body("Not authenticated");
    }

    // Get the email from the authenticated principal
    org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) auth
        .getPrincipal();
    String email = principal.getUsername();

    // Fetch the user from the database using the email
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found: " + email));

    // Extract the role (removing the "ROLE_" prefix)
    String role = principal.getAuthorities().stream()
        .findFirst()
        .map(granted -> granted.getAuthority().replace("ROLE_", ""))
        .orElse("UNKNOWN");

    // Build the response with firstName, lastName, email, and role
    Map<String, Object> response = new HashMap<>();
    response.put("firstName", user.getFirstName());
    response.put("lastName", user.getLastName());
    response.put("email", email);
    response.put("role", role);

    return ResponseEntity.ok(response);
  }
}