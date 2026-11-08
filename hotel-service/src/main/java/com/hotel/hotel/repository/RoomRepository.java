package com.hotel.hotel.repository;

import com.hotel.hotel.entity.Room;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Репозиторий для работы с номерами */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT r FROM Room r WHERE r.id = :id")
  Optional<Room> findByIdForUpdate(@Param("id") Long id);

  List<Room> findByAvailableTrueOrderByTimesBookedAscIdAsc();

  List<Room> findByAvailableTrue();

  List<Room> findByHotelId(Long hotelId);

  @Query(
      """
        SELECT r FROM Room r
        WHERE r.available = true
        AND NOT EXISTS (
            SELECT b FROM RoomBlock b
            WHERE b.room = r
            AND b.status != 'RELEASED'
            AND b.startDate < :endDate
            AND b.endDate > :startDate
        )
        ORDER BY r.id ASC
    """)
  List<Room> findAvailableRoomsByDates(
      @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

  @Query(
      """
        SELECT r FROM Room r
        WHERE r.available = true
        AND NOT EXISTS (
            SELECT b FROM RoomBlock b
            WHERE b.room = r
            AND b.status != 'RELEASED'
            AND b.startDate < :endDate
            AND b.endDate > :startDate
        )
        ORDER BY r.timesBooked ASC, r.id ASC
    """)
  List<Room> findRecommendedRoomsByDates(
      @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
