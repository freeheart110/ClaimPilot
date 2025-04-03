package com.bruceyulin.claimpilot.repository;

import com.bruceyulin.claimpilot.model.PolicyHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PolicyHolderRepository extends JpaRepository<PolicyHolder, Long> {

    Optional<PolicyHolder> findByFirstNameAndLastNameAndEmailAndPhone(
            String firstName,
            String lastName,
            String email,
            String phone);
}