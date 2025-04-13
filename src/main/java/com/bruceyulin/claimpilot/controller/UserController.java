package com.bruceyulin.claimpilot.controller;

import com.bruceyulin.claimpilot.dto.UserDTO;
import com.bruceyulin.claimpilot.mapper.UserMapper;
import com.bruceyulin.claimpilot.model.Role; // Import the Role enum
import com.bruceyulin.claimpilot.model.User;
import com.bruceyulin.claimpilot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
    Optional<User> user = userService.getUserById(id);
    return user.map(UserMapper::toDTO)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
    User saved = userService.saveUser(UserMapper.toEntity(userDTO));
    return ResponseEntity.ok(UserMapper.toDTO(saved));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/adjusters")
  public List<UserDTO> getAllAdjusters() {
    return userService.getUsersByRole(Role.ADJUSTER)
        .stream()
        .map(UserMapper::toDTO)
        .collect(Collectors.toList());
  }
}