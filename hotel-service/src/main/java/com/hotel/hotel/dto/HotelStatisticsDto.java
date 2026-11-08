package com.hotel.hotel.dto;

/** DTO для статистики отеля */
public record HotelStatisticsDto(
    Long hotelId, String hotelName, long totalRooms, long availableRooms, long totalBookings) {}
