package com.bruceyulin.claimpilot.mapper;

import com.bruceyulin.claimpilot.dto.PolicyHolderDTO;
import com.bruceyulin.claimpilot.model.PolicyHolder;

public class PolicyHolderMapper {

    public static PolicyHolder toEntity(PolicyHolderDTO dto) {
        if (dto == null)
            return null;

        return PolicyHolder.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .city(dto.getCity())
                .province(dto.getProvince())
                .postalCode(dto.getPostalCode())
                .driverLicenseNumber(dto.getDriverLicenseNumber())
                .vehicleVIN(dto.getVehicleVIN())
                .build();
    }

    public static PolicyHolderDTO toDTO(PolicyHolder entity) {
        if (entity == null)
            return null;

        return PolicyHolderDTO.builder()
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .city(entity.getCity())
                .province(entity.getProvince())
                .postalCode(entity.getPostalCode())
                .driverLicenseNumber(entity.getDriverLicenseNumber())
                .vehicleVIN(entity.getVehicleVIN())
                .build();
    }
}