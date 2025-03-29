package com.bruceyulin.claimpilot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "claim_number", nullable = false, unique = true)
    private String claimNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "claim_type", nullable = false)
    private ClaimType claimType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    @NotNull
    @Column(name = "claim_date", nullable = false)
    private LocalDate claimDate;

    @NotNull
    @Column(name = "date_of_accident", nullable = false)
    private LocalDate dateOfAccident;

    @NotBlank
    @Size(max = 5000)
    @Column(name = "accident_description", columnDefinition = "TEXT")
    private String accidentDescription;

    @Column(name = "police_report_number")
    private String policeReportNumber;

    @Column(name = "location_of_accident")
    private String locationOfAccident;

    @Column(name = "damage_description", columnDefinition = "TEXT")
    private String damageDescription;

    @DecimalMin(value = "0.0")
    @Column(name = "estimated_repair_cost", precision = 10, scale = 2)
    private BigDecimal estimatedRepairCost;

    @DecimalMin(value = "0.0")
    @Column(name = "final_settlement_amount", precision = 10, scale = 2)
    private BigDecimal finalSettlementAmount;

    @ManyToOne
    @JoinColumn(name = "policy_holder_id", nullable = false)
    private PolicyHolder policyHolder;
}
