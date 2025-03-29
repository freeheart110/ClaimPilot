package com.bruceyulin.claimpilot.controller;

import com.bruceyulin.claimpilot.model.PolicyHolder;
import com.bruceyulin.claimpilot.service.PolicyHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policyholders")
public class PolicyHolderController {

    @Autowired
    private PolicyHolderService service;

    @GetMapping
    public List<PolicyHolder> getAll() {
        return service.getAllPolicyHolders();
    }

    @PostMapping
    public PolicyHolder create(@RequestBody PolicyHolder policyHolder) {
        return service.savePolicyHolder(policyHolder);
    }

    @GetMapping("/{id}")
    public PolicyHolder getOne(@PathVariable Long id) {
        return service.getPolicyHolderById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deletePolicyHolder(id);
    }
}