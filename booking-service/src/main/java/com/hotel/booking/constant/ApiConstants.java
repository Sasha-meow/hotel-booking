package com.hotel.booking.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** API-константы */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiConstants {
  public static final String API_AUTH = "/api/auth";
  public static final String API_BOOKINGS = "/api/bookings";

  public static final String AUTH_REGISTER = "/register";
  public static final String AUTH_LOGIN = "/login";

  public static final String BOOKING_ID = "/{id}";
}
