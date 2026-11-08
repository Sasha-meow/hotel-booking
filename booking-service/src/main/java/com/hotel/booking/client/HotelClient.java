package com.hotel.booking.client;

import com.hotel.common.dto.AvailabilityRequest;
import com.hotel.common.dto.AvailabilityResponse;
import com.hotel.common.dto.RoomDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/** Feign-клиент для взаимодействия с Hotel Service */
@FeignClient(name = "hotel-service")
public interface HotelClient {

  // Получить рекомендованные комнаты (для автоподбора)
  @GetMapping("/api/rooms/recommend")
  List<RoomDto> getRecommendedRooms();

  // Подтвердить доступность (двухшаговая согласованность)
  @PostMapping("/api/rooms/{id}/confirm-availability")
  AvailabilityResponse confirmAvailability(
      @PathVariable("id") Long id, @RequestBody AvailabilityRequest request);

  // Освободить номер (компенсация)
  @PostMapping("/api/rooms/internal/{id}/release")
  void release(@PathVariable("id") Long id, @RequestParam("requestId") String requestId);
}
