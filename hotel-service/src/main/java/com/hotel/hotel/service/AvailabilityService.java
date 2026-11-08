package com.hotel.hotel.service;

import com.hotel.common.dto.AvailabilityRequest;
import com.hotel.common.dto.AvailabilityResponse;
import com.hotel.common.exception.ConflictException;
import com.hotel.common.exception.NotFoundException;
import com.hotel.hotel.entity.Room;
import com.hotel.hotel.entity.RoomBlock;
import com.hotel.hotel.repository.RoomBlockRepository;
import com.hotel.hotel.repository.RoomRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Сервис управления доступностью номеров */
@Slf4j
@Service
@RequiredArgsConstructor
public class AvailabilityService {
  private final RoomRepository roomRepository;
  private final RoomBlockRepository roomBlockRepository;

  @Transactional
  public AvailabilityResponse confirmAvailability(Long roomId, AvailabilityRequest request) {
    var existingBlock = roomBlockRepository.findByRoomIdAndRequestId(roomId, request.requestId());

    if (existingBlock.isPresent()) {
      log.info(
          "[INFO] [{}] Room already booked for this request (idempotency)", request.requestId());

      return new AvailabilityResponse(
          true, existingBlock.get().getId(), request.requestId(), "Already confirmed (idempotent)");
    }

    Room room =
        roomRepository
            .findByIdForUpdate(roomId)
            .orElseThrow(() -> new NotFoundException("Room not found with id " + roomId));

    if (!room.isAvailable()) {
      throw new ConflictException("Room is unavailable for booking (under maintenance)");
    }

    if (roomBlockRepository.existsOverlapping(roomId, request.startDate(), request.endDate())) {
      throw new ConflictException("Dates overlap with existing booking");
    }

    RoomBlock savedBlock = saveRoomBlock(room, request);

    room.setTimesBooked(room.getTimesBooked() + 1);
    roomRepository.save(room);

    log.info("[INFO] [{}] Room successfully blocked", request.requestId());
    return new AvailabilityResponse(true, savedBlock.getId(), request.requestId(), "Confirmed");
  }

  @Transactional
  public void release(Long roomId, String requestId) {
    roomBlockRepository
        .findByRoomIdAndRequestId(roomId, requestId)
        .ifPresent(
            block -> {
              block.setStatus(RoomBlock.BlockStatus.RELEASED);
              roomBlockRepository.save(block);
              log.info("[INFO] [{}] Block transitioned to RELEASED", requestId);
            });
  }

  private RoomBlock saveRoomBlock(Room room, AvailabilityRequest request) {
    RoomBlock block =
        RoomBlock.builder()
            .room(room)
            .startDate(request.startDate())
            .endDate(request.endDate())
            .requestId(request.requestId())
            .status(RoomBlock.BlockStatus.TEMPORARY)
            .createdAt(LocalDateTime.now())
            .build();

    return roomBlockRepository.save(block);
  }
}
