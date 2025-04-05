package com.bruceyulin.claimpilot.config;

import com.bruceyulin.claimpilot.model.Role;
import com.bruceyulin.claimpilot.model.User;
import com.bruceyulin.claimpilot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    if (userRepository.count() == 0) {
      User admin = User.builder()
          .firstName("Alice")
          .lastName("Admin")
          .email("admin@claimpilot.com")
          .password(passwordEncoder.encode("admin123"))
          .role(Role.ADMIN)
          .build();

      User adjuster = User.builder()
          .firstName("Bob")
          .lastName("Adjuster")
          .email("adjuster@claimpilot.com")
          .password(passwordEncoder.encode("adjuster123"))
          .role(Role.ADJUSTER)
          .build();

      userRepository.save(admin);
      userRepository.save(adjuster);

      System.out.println("✔ Default users added to the database.");
    }
  }
}