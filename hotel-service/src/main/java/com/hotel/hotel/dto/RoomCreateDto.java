package com.hotel.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** DTO для создания номера */
public record RoomCreateDto(
    @NotNull(message = "Hotel id is required") Long hotelId,
    @NotBlank(message = "Room number is required") String number,
    boolean available) {}
