package com.hotel.hotel.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO для создания отеля */
public record HotelCreateDto(
    @NotBlank(message = "Hotel name is required") String name,
    @NotBlank(message = "Hotel address is required") String address) {}
