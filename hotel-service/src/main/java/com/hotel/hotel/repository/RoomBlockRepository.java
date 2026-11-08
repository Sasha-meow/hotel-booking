package com.hotel.hotel.repository;

import com.hotel.hotel.entity.RoomBlock;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Репозиторий для работы с блокировками номеров */
@Repository
public interface RoomBlockRepository extends JpaRepository<RoomBlock, Long> {
  Optional<RoomBlock> findByRoomIdAndRequestId(Long roomId, String requestId);

  @Query(
      """
        SELECT COUNT(rb) > 0 FROM RoomBlock rb
        WHERE rb.room.id = :roomId
          AND rb.status != 'RELEASED'
          AND rb.startDate < :endDate
          AND rb.endDate > :startDate
    """)
  boolean existsOverlapping(
      @Param("roomId") Long roomId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
}
