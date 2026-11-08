package com.hotel.hotel.service;

import com.hotel.common.dto.RoomDto;
import com.hotel.common.exception.NotFoundException;
import com.hotel.hotel.dto.RoomCreateDto;
import com.hotel.hotel.entity.Hotel;
import com.hotel.hotel.entity.Room;
import com.hotel.hotel.repository.HotelRepository;
import com.hotel.hotel.repository.RoomRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Сервис для управления номерами */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {
  private final RoomRepository roomRepository;
  private final HotelRepository hotelRepository;

  @Transactional(readOnly = true)
  public List<RoomDto> getAllAvailable() {
    return roomRepository.findByAvailableTrue().stream().map(this::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<RoomDto> getAllAvailable(LocalDate startDate, LocalDate endDate) {
    if (isDatesEmpty(startDate, endDate)) {
      return getAllAvailable();
    }
    validateDates(startDate, endDate);

    log.info("[INFO] Retrieving available rooms for dates {} to {}", startDate, endDate);
    return roomRepository.findAvailableRoomsByDates(startDate, endDate).stream()
        .map(this::toDto)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<RoomDto> getRecommended() {
    return roomRepository.findByAvailableTrueOrderByTimesBookedAscIdAsc().stream()
        .map(this::toDto)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<RoomDto> getRecommended(LocalDate startDate, LocalDate endDate) {
    if (isDatesEmpty(startDate, endDate)) {
      return getRecommended();
    }
    validateDates(startDate, endDate);

    log.info("[INFO] Retrieving recommended rooms for dates {} to {}", startDate, endDate);
    return roomRepository.findRecommendedRoomsByDates(startDate, endDate).stream()
        .map(this::toDto)
        .toList();
  }

  @Transactional
  public RoomDto create(RoomCreateDto dto) {
    Hotel hotel =
        hotelRepository
            .findById(dto.hotelId())
            .orElseThrow(() -> new NotFoundException("Hotel not found with id " + dto.hotelId()));

    Room room =
        Room.builder()
            .hotel(hotel)
            .number(dto.number())
            .available(dto.available())
            .timesBooked(0L)
            .build();

    return toDto(roomRepository.save(room));
  }

  private boolean isDatesEmpty(LocalDate startDate, LocalDate endDate) {
    if ((startDate == null && endDate != null) || (startDate != null && endDate == null)) {
      throw new IllegalArgumentException("Both startDate and endDate must be provided together");
    }

    return startDate == null && endDate == null;
  }

  private void validateDates(LocalDate startDate, LocalDate endDate) {
    if (startDate.isAfter(endDate) || startDate.isEqual(endDate)) {
      throw new IllegalArgumentException("Start date must be strictly before end date");
    }
  }

  private RoomDto toDto(Room room) {
    return new RoomDto(
        room.getId(),
        room.getHotel().getId(),
        room.getHotel().getName(),
        room.getNumber(),
        room.isAvailable(),
        room.getTimesBooked());
  }
}
