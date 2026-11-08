package com.hotel.booking.service;

import com.hotel.booking.client.HotelClient;
import com.hotel.common.dto.AvailabilityRequest;
import com.hotel.common.dto.AvailabilityResponse;
import com.hotel.common.dto.RoomDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/** Сервис повторных попыток для Feign */
@Slf4j
@Service
@RequiredArgsConstructor
public class RetryService {

  private final HotelClient hotelClient;
  private static final int MAX_ATTEMPTS = 3;
  private static final int DELAY = 500;

  /** Подтверждение доступности (3 попытки) */
  @Retryable(
      retryFor = {Exception.class},
      maxAttempts = MAX_ATTEMPTS,
      backoff = @Backoff(delay = DELAY))
  public AvailabilityResponse confirmAvailability(Long roomId, AvailabilityRequest request) {
    log.info("[INFO] [{}] Attempting confirmAvailability for room {}", request.requestId(), roomId);
    return hotelClient.confirmAvailability(roomId, request);
  }

  /** Обработка исчерпания попыток подтверждения */
  @Recover
  public AvailabilityResponse recoverConfirm(
      Exception error, Long roomId, AvailabilityRequest request) {
    log.error(
        "[ERROR] [{}] All attempts failed for confirmAvailability", request.requestId(), error);
    throw new RuntimeException(
        "Hotel Service is unavailable after 3 attempts: " + error.getMessage());
  }

  /** Освобождение номера (3 попытки) */
  @Retryable(
      retryFor = {Exception.class},
      maxAttempts = MAX_ATTEMPTS,
      backoff = @Backoff(delay = DELAY))
  public void release(Long roomId, String requestId) {
    log.info("[INFO] [{}] Attempting release for room {}", requestId, roomId);
    hotelClient.release(roomId, requestId);
  }

  /** Обработка исчерпания попыток освобождения номера */
  @Recover
  public void recoverRelease(Exception error, Long roomId, String requestId) {
    log.error("[ERROR] [{}] Failed to release room {} after 3 attempts", requestId, roomId, error);
  }

  /** Получение рекомендованных комнат (3 попытки) */
  @Retryable(
      retryFor = {Exception.class},
      maxAttempts = MAX_ATTEMPTS,
      backoff = @Backoff(delay = DELAY))
  public List<RoomDto> getRecommendedRooms() {
    log.info("[INFO] Requesting recommended rooms");
    return hotelClient.getRecommendedRooms();
  }

  /** Обработка исчерпания попыток получения рекомендаций */
  @Recover
  public List<RoomDto> recoverRecommended(Exception error) {
    log.error("[ERROR] Failed to get recommended rooms", error);
    throw new RuntimeException("Hotel Service is unavailable for recommendations");
  }
}
