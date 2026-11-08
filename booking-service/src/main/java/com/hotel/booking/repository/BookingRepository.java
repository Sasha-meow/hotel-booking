package com.hotel.booking.repository;

import com.hotel.booking.entity.Booking;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Репозиторий для работы с бронированиями */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
  Optional<Booking> findByRequestId(String requestId);

  Page<Booking> findByUserId(Long userId, Pageable pageable);
}
