package com.bruceyulin.claimpilot.service;

import com.bruceyulin.claimpilot.model.PolicyHolder;
import com.bruceyulin.claimpilot.repository.PolicyHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyHolderService {

    @Autowired
    private PolicyHolderRepository repository;

    public List<PolicyHolder> getAllPolicyHolders() {
        return repository.findAll();
    }

    public PolicyHolder savePolicyHolder(PolicyHolder policyHolder) {
        return repository.save(policyHolder);
    }

    public PolicyHolder getPolicyHolderById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deletePolicyHolder(Long id) {
        repository.deleteById(id);
    }

    public PolicyHolder findOrCreate(String firstName, String lastName, String email, String phone,
            String address, String city, String province, String postalCode, String driverLicenseNumber,
            String vehicleVIN) {

        Optional<PolicyHolder> existing = repository.findByFirstNameAndLastNameAndEmailAndPhone(
                firstName, lastName, email, phone);

        if (existing.isPresent()) {
            return existing.get();
        }

        PolicyHolder newHolder = PolicyHolder.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .address(address)
                .city(city)
                .province(province)
                .postalCode(postalCode)
                .driverLicenseNumber(driverLicenseNumber)
                .vehicleVIN(vehicleVIN)
                .build();

        return repository.save(newHolder);
    }

}