package com.bruceyulin.claimpilot.repository;

import com.bruceyulin.claimpilot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bruceyulin.claimpilot.model.Claim;
import com.bruceyulin.claimpilot.model.PolicyHolder;
import com.bruceyulin.claimpilot.model.Role;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  List<User> findByRole(Role role);

  @Query("SELECT u.id FROM User u WHERE u.role = 'ADJUSTER' ORDER BY " +
      "(SELECT COUNT(c) FROM Claim c WHERE c.assignedAdjuster = u AND c.status NOT IN ('CLOSED', 'CANCELLED')) ASC")
  List<Long> findAdjustersByLeastClaims();
}
