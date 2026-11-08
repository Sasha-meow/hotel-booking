package com.hotel.booking.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO для запроса аутентификации */
public record UserLoginDto(@NotBlank String username, @NotBlank String password) {}
