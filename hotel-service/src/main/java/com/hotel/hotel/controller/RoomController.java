package com.hotel.hotel.controller;

import com.hotel.common.dto.AvailabilityRequest;
import com.hotel.common.dto.AvailabilityResponse;
import com.hotel.common.dto.RoomDto;
import com.hotel.hotel.constant.ApiConstants;
import com.hotel.hotel.dto.RoomCreateDto;
import com.hotel.hotel.service.AvailabilityService;
import com.hotel.hotel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** REST-контроллер для управления номерами */
@Tag(name = "Rooms", description = "Room management")
@RestController
@RequestMapping(ApiConstants.API_ROOMS)
@RequiredArgsConstructor
public class RoomController {
  private final RoomService roomService;
  private final AvailabilityService availabilityService;

  @Operation(summary = "Get all available rooms")
  @GetMapping
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public List<RoomDto> getAllAvailable(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {
    return roomService.getAllAvailable(startDate, endDate);
  }

  @Operation(summary = "Get recommended rooms")
  @GetMapping(ApiConstants.ROOM_RECOMMEND)
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public List<RoomDto> getRecommended(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {
    return roomService.getRecommended(startDate, endDate);
  }

  @Operation(summary = "Create a new room")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  public RoomDto create(@Valid @RequestBody RoomCreateDto dto) {
    return roomService.create(dto);
  }

  @Operation(summary = "Confirm room availability")
  @PostMapping(ApiConstants.ROOM_CONFIRM_AVAILABILITY)
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public AvailabilityResponse confirmAvailability(
      @PathVariable Long id, @Valid @RequestBody AvailabilityRequest request) {
    return availabilityService.confirmAvailability(id, request);
  }

  @Operation(summary = "Release room")
  @PostMapping(ApiConstants.ROOM_RELEASE)
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void release(@PathVariable Long id, @RequestParam String requestId) {
    availabilityService.release(id, requestId);
  }
}
