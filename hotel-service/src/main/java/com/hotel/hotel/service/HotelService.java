package com.hotel.hotel.service;

import com.hotel.common.exception.NotFoundException;
import com.hotel.hotel.dto.HotelCreateDto;
import com.hotel.hotel.dto.HotelDto;
import com.hotel.hotel.dto.HotelStatisticsDto;
import com.hotel.hotel.entity.Hotel;
import com.hotel.hotel.entity.Room;
import com.hotel.hotel.repository.HotelRepository;
import com.hotel.hotel.repository.RoomRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Сервис для управления отелями */
@Slf4j
@Service
@RequiredArgsConstructor
public class HotelService {
  private final HotelRepository hotelRepository;
  private final RoomRepository roomRepository;

  @Transactional(readOnly = true)
  public List<HotelDto> getAll() {
    return hotelRepository.findAll().stream().map(this::toDto).toList();
  }

  @Transactional(readOnly = true)
  public HotelDto getById(Long id) {
    return toDto(findHotel(id));
  }

  @Transactional
  public HotelDto create(HotelCreateDto dto) {
    Hotel hotel = Hotel.builder().name(dto.name()).address(dto.address()).build();

    return toDto(hotelRepository.save(hotel));
  }

  @Transactional
  public HotelDto update(Long id, HotelCreateDto dto) {
    Hotel hotel = findHotel(id);
    hotel.setName(dto.name());
    hotel.setAddress(dto.address());

    return toDto(hotelRepository.save(hotel));
  }

  @Transactional
  public void delete(Long id) {
    if (!hotelRepository.existsById(id)) {
      throw new NotFoundException("Hotel not found with id " + id);
    }
    hotelRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public HotelStatisticsDto getStatistics(Long hotelId) {
    Hotel hotel = findHotel(hotelId);

    List<Room> rooms = roomRepository.findByHotelId(hotelId);

    return buildStatistics(hotel, rooms);
  }

  private Hotel findHotel(Long id) {
    return hotelRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Hotel not found with id " + id));
  }

  private HotelStatisticsDto buildStatistics(Hotel hotel, List<Room> rooms) {
    long totalRooms = rooms.size();
    long availableRooms = rooms.stream().filter(Room::isAvailable).count();
    long totalBookings = rooms.stream().mapToLong(Room::getTimesBooked).sum();

    return new HotelStatisticsDto(
        hotel.getId(), hotel.getName(), totalRooms, availableRooms, totalBookings);
  }

  private HotelDto toDto(Hotel hotel) {
    return new HotelDto(hotel.getId(), hotel.getName(), hotel.getAddress());
  }
}
