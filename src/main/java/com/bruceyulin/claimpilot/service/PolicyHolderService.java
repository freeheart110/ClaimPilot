package com.bruceyulin.claimpilot.service;

import com.bruceyulin.claimpilot.model.PolicyHolder;
import com.bruceyulin.claimpilot.repository.PolicyHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}