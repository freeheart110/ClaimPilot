package com.bruceyulin.claimpilot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "policy_holders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email
    @Column(name = "email")
    private String email;

    @Pattern(regexp = "^[0-9\\-\\+\\(\\) ]{7,20}$", message = "Invalid phone number format")
    @Column(name = "phone_number")
    private String phone;

    @NotBlank
    @Column(nullable = false)
    private String address;

    @NotBlank
    @Column(nullable = false)
    private String city;

    @NotBlank
    @Column(nullable = false)
    private String province;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z]\\d[A-Za-z] ?\\d[A-Za-z]\\d$", message = "Invalid Canadian postal code")
    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @NotBlank
    @Column(name = "driver_license_number", nullable = false, unique = true)
    private String driverLicenseNumber;

    @Column(name = "vehicle_vin")
    private String vehicleVIN;
}