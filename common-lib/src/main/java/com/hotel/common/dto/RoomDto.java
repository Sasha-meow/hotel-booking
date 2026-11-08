package com.hotel.common.dto;

/** DTO с информацией о номере */
public record RoomDto(
    Long id, Long hotelId, String hotelName, String number, boolean available, long timesBooked) {}
