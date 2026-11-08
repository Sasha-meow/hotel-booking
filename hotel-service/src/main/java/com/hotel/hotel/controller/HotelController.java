package com.hotel.hotel.controller;

import com.hotel.hotel.constant.ApiConstants;
import com.hotel.hotel.dto.HotelCreateDto;
import com.hotel.hotel.dto.HotelDto;
import com.hotel.hotel.dto.HotelStatisticsDto;
import com.hotel.hotel.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** REST-контроллер для управления отелями */
@Tag(name = "Hotels", description = "Hotel management")
@RestController
@RequestMapping(ApiConstants.API_HOTELS)
@RequiredArgsConstructor
public class HotelController {
  private final HotelService hotelService;

  @Operation(summary = "Get all hotels")
  @GetMapping
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public List<HotelDto> getAll() {
    return hotelService.getAll();
  }

  @Operation(summary = "Get hotel by ID")
  @GetMapping(ApiConstants.HOTEL_ID)
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public HotelDto getById(@PathVariable Long id) {
    return hotelService.getById(id);
  }

  @Operation(summary = "Create a new hotel")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  public HotelDto create(@Valid @RequestBody HotelCreateDto dto) {
    return hotelService.create(dto);
  }

  @Operation(summary = "Update hotel by id")
  @PutMapping(ApiConstants.HOTEL_ID)
  @PreAuthorize("hasRole('ADMIN')")
  public HotelDto update(@PathVariable Long id, @Valid @RequestBody HotelCreateDto dto) {
    return hotelService.update(id, dto);
  }

  @Operation(summary = "Delete hotel by id")
  @DeleteMapping(ApiConstants.HOTEL_ID)
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    hotelService.delete(id);
  }

  @Operation(summary = "Get hotel statistics")
  @GetMapping(ApiConstants.HOTEL_STATISTICS)
  @PreAuthorize("hasRole('ADMIN')")
  public HotelStatisticsDto getStatistics(@PathVariable Long id) {
    return hotelService.getStatistics(id);
  }
}
