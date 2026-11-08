package com.hotel.booking.controller;

import com.hotel.booking.constant.ApiConstants;
import com.hotel.booking.constant.BaseConstants;
import com.hotel.booking.dto.BookingCreateDto;
import com.hotel.booking.dto.BookingDto;
import com.hotel.booking.service.BookingService;
import com.hotel.common.constant.SecurityConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** REST-контроллер для управления бронированиями */
@RestController
@RequestMapping(ApiConstants.API_BOOKINGS)
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking management")
public class BookingController {
  private final BookingService bookingService;

  @Operation(summary = "Create a new booking")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('USER')")
  public BookingDto create(
      @Valid @RequestBody BookingCreateDto dto,
      @RequestHeader(value = SecurityConstants.IDEMPOTENCY_KEY, required = false)
          String idempotencyKey,
      @AuthenticationPrincipal Long userId) {
    return bookingService.createBooking(userId, dto, idempotencyKey);
  }

  @Operation(summary = "Get user booking history")
  @GetMapping
  @PreAuthorize("hasRole('USER')")
  public Page<BookingDto> getUserBookings(
      @AuthenticationPrincipal Long userId,
      @RequestParam(defaultValue = BaseConstants.DEFAULT_PAGE_VALUE) int page,
      @RequestParam(defaultValue = BaseConstants.DEFAULT_PAGE_SIZE) int size) {
    return bookingService.getUserBookings(userId, page, size);
  }

  @Operation(summary = "Get booking by ID")
  @GetMapping(ApiConstants.BOOKING_ID)
  @PreAuthorize("hasRole('USER')")
  public BookingDto getBooking(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
    return bookingService.getBooking(id, userId);
  }

  @Operation(summary = "Cancel booking")
  @DeleteMapping(ApiConstants.BOOKING_ID)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('USER')")
  public void cancelBooking(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
    bookingService.cancelBooking(id, userId);
  }
}
