package com.bruceyulin.claimpilot.dto;

import com.bruceyulin.claimpilot.model.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private Role role;
}