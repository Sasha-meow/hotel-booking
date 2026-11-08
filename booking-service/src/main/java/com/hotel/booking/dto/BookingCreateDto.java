package com.hotel.booking.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** DTO для создания бронирования */
public record BookingCreateDto(
    Long roomId,
    @NotNull(message = "Start date is required") LocalDate startDate,
    @NotNull(message = "End date is required") LocalDate endDate,
    @NotNull(message = "autoSelect is required") Boolean autoSelect) {}
