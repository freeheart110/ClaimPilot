package com.bruceyulin.claimpilot.dto;

import lombok.Data;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyHolderDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private String driverLicenseNumber;
    private String vehicleVIN;
}
