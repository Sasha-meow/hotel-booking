package com.hotel.booking.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO для запроса регистрации */
public record UserRegisterDto(@NotBlank String username, @NotBlank String password) {}
