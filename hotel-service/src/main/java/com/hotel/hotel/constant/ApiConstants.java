package com.hotel.hotel.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** API-константы */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiConstants {
  public static final String API_HOTELS = "/api/hotels";
  public static final String API_ROOMS = "/api/rooms";

  public static final String HOTEL_ID = "/{id}";
  public static final String HOTEL_STATISTICS = "/{id}/statistics";

  public static final String ROOM_RECOMMEND = "/recommend";
  public static final String ROOM_CONFIRM_AVAILABILITY = "/{id}/confirm-availability";
  public static final String ROOM_RELEASE = "/internal/{id}/release";
}
