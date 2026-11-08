package com.hotel.booking.service;

import com.hotel.booking.constant.BookingStatus;
import com.hotel.booking.dto.BookingCreateDto;
import com.hotel.booking.dto.BookingDto;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.common.dto.AvailabilityRequest;
import com.hotel.common.dto.AvailabilityResponse;
import com.hotel.common.dto.RoomDto;
import com.hotel.common.exception.ConflictException;
import com.hotel.common.exception.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Сервис для работы с бронированиями */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
  private final BookingRepository bookingRepository;
  private final RetryService retryService;

  /** Создает бронирование и выполняет сагу */
  @Transactional
  public BookingDto createBooking(Long userId, BookingCreateDto dto, String idempotencyKey) {
    if (dto.startDate().isAfter(dto.endDate()) || dto.startDate().isEqual(dto.endDate())) {
      throw new IllegalArgumentException("Start date must be strictly before end date");
    }

    String requestId = getOrConfigureRequestId(idempotencyKey);

    var existing = bookingRepository.findByRequestId(requestId);
    if (existing.isPresent()) {
      log.info("[INFO] [{}] Booking already exists (idempotency)", requestId);
      return toDto(existing.get());
    }

    Long roomId = resolveRoomId(dto, requestId);

    Booking booking = saveNewBooking(userId, roomId, dto, requestId);

    try {
      AvailabilityRequest request =
          new AvailabilityRequest(dto.startDate(), dto.endDate(), requestId);
      AvailabilityResponse response = retryService.confirmAvailability(roomId, request);

      if (response.confirmed()) {
        booking.setStatus(BookingStatus.CONFIRMED);
        log.info("[INFO] [{}] Booking transitioned to CONFIRMED", requestId);
      } else {
        booking.setStatus(BookingStatus.CANCELLED);
        log.warn("[WARN] [{}] Booking cancelled (Hotel Service denied)", requestId);
      }
    } catch (Exception error) {
      booking.setStatus(BookingStatus.CANCELLED);
      log.info("[INFO] [{}] Starting compensation (release)", requestId);
      performCompensation(roomId, requestId);
    }

    return toDto(bookingRepository.save(booking));
  }

  /** Отменяет бронирование и освобождает номер */
  @Transactional
  public void cancelBooking(Long id, Long userId) {
    Booking booking = findBookingById(id, userId);

    if (booking.getStatus() == BookingStatus.CANCELLED) {
      return;
    }

    log.info("[INFO] [{}] Cancelling booking", booking.getRequestId());
    booking.setStatus(BookingStatus.CANCELLED);

    bookingRepository.save(booking);

    performCompensation(booking.getRoomId(), booking.getRequestId());
  }

  /** Возвращает историю бронирований пользователя */
  @Transactional(readOnly = true)
  public Page<BookingDto> getUserBookings(Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return bookingRepository.findByUserId(userId, pageable).map(this::toDto);
  }

  /** Возвращает бронирование по ID */
  @Transactional(readOnly = true)
  public BookingDto getBooking(Long id, Long userId) {
    Booking booking = findBookingById(id, userId);

    return toDto(booking);
  }

  /** Возвращает requestId или генерирует новый */
  private String getOrConfigureRequestId(String idempotencyKey) {
    return (idempotencyKey != null && !idempotencyKey.isBlank())
        ? idempotencyKey
        : UUID.randomUUID().toString();
  }

  /** Сохраняет новое бронирование в статусе PENDING */
  private Booking saveNewBooking(Long userId, Long roomId, BookingCreateDto dto, String requestId) {
    Booking booking =
        Booking.builder()
            .userId(userId)
            .roomId(roomId)
            .startDate(dto.startDate())
            .endDate(dto.endDate())
            .status(BookingStatus.PENDING)
            .requestId(requestId)
            .createdAt(LocalDateTime.now())
            .build();

    return bookingRepository.save(booking);
  }

  /** Ищет бронь по ID и проверяет владельца */
  private Booking findBookingById(Long id, Long userId) {
    Booking booking =
        bookingRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

    if (!booking.getUserId().equals(userId)) {
      throw new ConflictException("Access denied");
    }

    return booking;
  }

  /** Выдаёт id комнаты (вручную или автоподбор) */
  private Long resolveRoomId(BookingCreateDto dto, String requestId) {
    if (!dto.autoSelect()) {
      if (dto.roomId() == null) {
        throw new IllegalArgumentException("Specify roomId or set autoSelect=true");
      }
      return dto.roomId();
    }

      List<RoomDto> rooms;
      try {
          rooms = retryService.getRecommendedRooms();
      } catch (Exception error) {
          throw new ConflictException("Hotel Service is temporarily unavailable. Cannot auto select a room.");
      }

      if (rooms.isEmpty()) {
          throw new ConflictException("No available rooms found");
      }

      Long selectedRoomId = rooms.get(0).id();
      log.info("[INFO] [{}] Auto-selected room {}", requestId, selectedRoomId);
      return selectedRoomId;
  }

  /** Выполняет компенсацию (освобождение номера) */
  private void performCompensation(Long roomId, String requestId) {
    log.info("[INFO] [{}] Starting compensation (release) for room {}", requestId, roomId);
    try {
      retryService.release(roomId, requestId);
    } catch (Exception ignored) {
    }
  }

  /** Преобразует сущность в DTO */
  private BookingDto toDto(Booking booking) {
    return new BookingDto(
        booking.getId(),
        booking.getUserId(),
        booking.getRoomId(),
        booking.getStartDate(),
        booking.getEndDate(),
        booking.getStatus(),
        booking.getRequestId(),
        booking.getCreatedAt());
  }
}
