package com.bruceyulin.claimpilot.repository;

import com.bruceyulin.claimpilot.model.PolicyHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyHolderRepository extends JpaRepository<PolicyHolder, Long> {
    // You get CRUD for free here!
}