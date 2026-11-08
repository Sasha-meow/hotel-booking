package com.hotel.booking.controller;

import com.hotel.booking.constant.ApiConstants;
import com.hotel.booking.dto.TokenResponse;
import com.hotel.booking.dto.UserLoginDto;
import com.hotel.booking.dto.UserRegisterDto;
import com.hotel.booking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** REST-контроллер для авторизации */
@RestController
@RequestMapping(ApiConstants.API_AUTH)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login")
public class UserController {
  private final UserService userService;

  @Operation(summary = "Register a new user")
  @PostMapping(ApiConstants.AUTH_REGISTER)
  @ResponseStatus(HttpStatus.CREATED)
  public TokenResponse register(@Valid @RequestBody UserRegisterDto dto) {
    String token = userService.register(dto);

    return new TokenResponse(token);
  }

  @Operation(summary = "Authenticate user")
  @PostMapping(ApiConstants.AUTH_LOGIN)
  public TokenResponse auth(@Valid @RequestBody UserLoginDto dto) {
    String token = userService.authenticate(dto);

    return new TokenResponse(token);
  }
}
