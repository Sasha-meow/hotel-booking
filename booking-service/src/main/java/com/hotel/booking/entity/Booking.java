package com.hotel.booking.entity;

import com.hotel.booking.constant.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

/** Сущность бронирования */
@Entity
@Table(
    name = "bookings",
    uniqueConstraints = @UniqueConstraint(name = "uc_booking_request", columnNames = "request_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "room_id", nullable = false)
  private Long roomId;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookingStatus status;

  @Column(name = "request_id", nullable = false)
  private String requestId;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
