package com.hotel.common.dto;

/** DTO ответа на подтверждение доступности номера */
public record AvailabilityResponse(
    boolean confirmed, Long blockId, String requestId, String reason) {}
