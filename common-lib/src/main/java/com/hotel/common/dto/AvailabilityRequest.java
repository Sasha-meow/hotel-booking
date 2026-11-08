package com.hotel.common.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** DTO запроса на подтверждение доступности номера */
public record AvailabilityRequest(
    @NotNull(message = "Start date is required") LocalDate startDate,
    @NotNull(message = "End date is required") LocalDate endDate,
    @NotNull(message = "Request ID is required") String requestId) {}
