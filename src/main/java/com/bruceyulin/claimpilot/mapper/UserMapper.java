package com.bruceyulin.claimpilot.mapper;

import com.bruceyulin.claimpilot.dto.UserDTO;
import com.bruceyulin.claimpilot.model.User;

public class UserMapper {

  public static UserDTO toDTO(User user) {
    if (user == null)
      return null;

    return UserDTO.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .role(user.getRole())
        .build();
  }

  public static User toEntity(UserDTO dto) {
    if (dto == null)
      return null;

    return User.builder()
        .id(dto.getId())
        .firstName(dto.getFirstName())
        .lastName(dto.getLastName())
        .email(dto.getEmail())
        .role(dto.getRole())
        .build();
  }
}