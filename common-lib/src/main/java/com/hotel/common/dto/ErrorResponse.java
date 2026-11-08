package com.hotel.common.dto;

import java.time.LocalDateTime;

/** Стандартный DTO для ответов с ошибками */
public record ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {}
