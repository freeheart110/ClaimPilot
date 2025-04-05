package com.bruceyulin.claimpilot.config;

import com.bruceyulin.claimpilot.model.User;
import com.bruceyulin.claimpilot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.List;

@Configuration
@EnableMethodSecurity // Enables @PreAuthorize, @Secured, etc.
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserRepository userRepository;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/adjuster/**").hasRole("ADJUSTER")
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/login") // custom login page
            .defaultSuccessUrl("/dashboard", true) // where to go after successful login
            .permitAll())
        .logout(logout -> logout
            .logoutSuccessUrl("/login?logout") // where to go after logout
            .permitAll())
        .csrf(csrf -> csrf.disable()); // Disable CSRF for now (optional; adjust for production)

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return email -> {
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

      // Spring Security expects roles to be prefixed with "ROLE_"
      return new org.springframework.security.core.userdetails.User(
          user.getEmail(),
          user.getPassword(),
          List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    };
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // secure password hashing
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}