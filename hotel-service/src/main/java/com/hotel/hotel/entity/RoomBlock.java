package com.hotel.hotel.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

/** Сущность блокировки дат номера */
@Entity
@Table(
    name = "room_blocks",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uc_room_request",
            columnNames = {"room_id", "request_id"}),
    indexes = @Index(name = "idx_block_dates", columnList = "room_id, start_date, end_date"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomBlock {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(name = "request_id", nullable = false)
  private String requestId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BlockStatus status;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  public enum BlockStatus {
    TEMPORARY,
    CONFIRMED,
    RELEASED
  }
}
