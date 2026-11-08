package com.hotel.booking.dto;

import com.hotel.booking.constant.BookingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** DTO для ответа с данными бронирования */
public record BookingDto(
    Long id,
    Long userId,
    Long roomId,
    LocalDate startDate,
    LocalDate endDate,
    BookingStatus status,
    String requestId,
    LocalDateTime createdAt) {}
