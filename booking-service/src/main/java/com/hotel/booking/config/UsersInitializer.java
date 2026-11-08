package com.hotel.booking.config;

import com.hotel.booking.constant.Role;
import com.hotel.booking.entity.User;
import com.hotel.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsersInitializer implements CommandLineRunner {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    String adminUsername = System.getenv().getOrDefault("BOOTSTRAP_ADMIN_USER", "admin");
    String adminPassword = System.getenv().getOrDefault("BOOTSTRAP_ADMIN_PASS", "admin");

    String userUsername = System.getenv().getOrDefault("BOOTSTRAP_USER_USER", "user");
    String userPassword = System.getenv().getOrDefault("BOOTSTRAP_USER_PASS", "user");

    createUserIfNotFound(adminUsername, adminPassword, Role.ADMIN);
    createUserIfNotFound(userUsername, userPassword, Role.USER);
  }

  private void createUserIfNotFound(String username, String password, Role role) {
    if (userRepository.findByUsername(username).isEmpty()) {
      User user =
          User.builder()
              .username(username)
              .password(passwordEncoder.encode(password))
              .role(role)
              .build();
      userRepository.save(user);
      log.info("[INFO] Bootstrap user created: {} (Role: {})", username, role);
    } else {
      log.info("[INFO] Bootstrap user already exists: {}", username);
    }
  }
}
