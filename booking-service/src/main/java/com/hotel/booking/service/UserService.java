package com.hotel.booking.service;

import com.hotel.booking.constant.Role;
import com.hotel.booking.dto.UserLoginDto;
import com.hotel.booking.dto.UserRegisterDto;
import com.hotel.booking.entity.User;
import com.hotel.booking.repository.UserRepository;
import com.hotel.booking.security.JwtService;
import com.hotel.common.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Сервис для регистрации и аутентификации */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  /** Регистрирует пользователя и возвращает JWT */
  @Transactional
  public String register(UserRegisterDto dto) {
    if (userRepository.existsByUsername(dto.username())) {
      throw new ConflictException("User already exists");
    }

    User user =
        User.builder()
            .username(dto.username())
            .password(passwordEncoder.encode(dto.password()))
            .role(Role.USER)
            .build();

    userRepository.save(user);

    return jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
  }

  /** Аутентифицирует пользователя и возвращает JWT */
  @Transactional(readOnly = true)
  public String authenticate(UserLoginDto dto) {
    User user =
        userRepository
            .findByUsername(dto.username())
            .orElseThrow(() -> new BadCredentialsException("Invalid login or password"));

    if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
      throw new BadCredentialsException("Invalid login or password");
    }

    return jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
  }
}
