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

    public PolicyHolder findOrCreate(String firstName, String lastName, String email, String phone) {
        Optional<PolicyHolder> existing = repository.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }
        PolicyHolder newHolder = new PolicyHolder();
        newHolder.setFirstName(firstName);
        newHolder.setLastName(lastName);
        newHolder.setEmail(email);
        newHolder.setPhone(phone);
        return repository.save(newHolder);
    }

    public Optional<PolicyHolder> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}