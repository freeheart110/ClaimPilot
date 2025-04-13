package com.bruceyulin.claimpilot.config;

import com.bruceyulin.claimpilot.model.*;
import com.bruceyulin.claimpilot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ClaimRepository claimRepository;

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

      System.out.println("\u2714 Default users added to the database.");
    }

    if (claimRepository.count() == 0) {
      User adjuster = userRepository.findByEmail("adjuster@claimpilot.com").orElseThrow();

      claimRepository.saveAll(List.of(
          Claim.builder()
              .claimNumber("CLM1001")
              .claimType(ClaimType.COLLISION)
              .status(ClaimStatus.SUBMITTED)
              .claimDate(LocalDate.now())
              .dateOfAccident(LocalDate.now().minusDays(3))
              .accidentDescription("Rear-end collision at intersection.")
              .policeReportNumber("RPT-001")
              .locationOfAccident("Regina, SK")
              .damageDescription("Rear bumper damage")
              .estimatedRepairCost(BigDecimal.valueOf(1200))
              .finalSettlementAmount(null)
              .policyHolder(PolicyHolder.builder()
                  .firstName("Yu")
                  .lastName("Lin")
                  .email("freeheart110@gmail.com")
                  .phone("3065965066")
                  .address("5169 Buckingham Drive E")
                  .city("Regina")
                  .province("SK")
                  .postalCode("S4V 3A4")
                  .driverLicenseNumber("D123456789")
                  .vehicleVIN("1HGCM82633A123456")
                  .build())
              .assignedAdjuster(adjuster)
              .build(),

          Claim.builder()
              .claimNumber("CLM1002")
              .claimType(ClaimType.THEFT)
              .status(ClaimStatus.IN_REVIEW)
              .claimDate(LocalDate.now().minusDays(1))
              .dateOfAccident(LocalDate.now().minusDays(5))
              .accidentDescription("Vehicle stolen from parking garage.")
              .policeReportNumber("RPT-002")
              .locationOfAccident("Toronto, ON")
              .damageDescription("Missing entire vehicle")
              .estimatedRepairCost(BigDecimal.valueOf(15000))
              .finalSettlementAmount(null)
              .policyHolder(PolicyHolder.builder()
                  .firstName("Jane")
                  .lastName("Smith")
                  .email("jane@example.com")
                  .phone("5551234567")
                  .address("123 Maple Street")
                  .city("Toronto")
                  .province("ON")
                  .postalCode("M5G 1X8")
                  .driverLicenseNumber("S987654321")
                  .vehicleVIN("2FAFP71W45X123456")
                  .build())
              .assignedAdjuster(adjuster)
              .build(),

          Claim.builder()
              .claimNumber("CLM1003")
              .claimType(ClaimType.VANDALISM)
              .status(ClaimStatus.APPROVED)
              .claimDate(LocalDate.now().minusDays(7))
              .dateOfAccident(LocalDate.now().minusDays(10))
              .accidentDescription("Windows smashed and doors keyed.")
              .policeReportNumber("RPT-003")
              .locationOfAccident("Vancouver, BC")
              .damageDescription("Broken windows, deep scratches")
              .estimatedRepairCost(BigDecimal.valueOf(2500))
              .finalSettlementAmount(BigDecimal.valueOf(2400))
              .policyHolder(PolicyHolder.builder()
                  .firstName("Tom")
                  .lastName("Lee")
                  .email("tom@example.com")
                  .phone("6045557890")
                  .address("99 Burrard St")
                  .city("Vancouver")
                  .province("BC")
                  .postalCode("V6C 3L6")
                  .driverLicenseNumber("B135792468")
                  .vehicleVIN("3VWFE21C04M123456")
                  .build())
              .assignedAdjuster(adjuster)
              .build()));

      System.out.println("\u2714 Dummy claims and policyholders added.");
    }
  }
}
