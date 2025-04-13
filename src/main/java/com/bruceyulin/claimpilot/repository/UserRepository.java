package com.bruceyulin.claimpilot.repository;

import com.bruceyulin.claimpilot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bruceyulin.claimpilot.model.Role;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  List<User> findByRole(Role role);
}
