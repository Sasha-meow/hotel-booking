package com.hotel.hotel.entity;

import jakarta.persistence.*;
import lombok.*;

/** Сущность номера */
@Entity
@Table(
    name = "rooms",
    indexes = {
      @Index(name = "idx_room_hotel", columnList = "hotel_id"),
      @Index(name = "idx_room_times_booked", columnList = "times_booked")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", nullable = false)
  private Hotel hotel;

  @Column(nullable = false)
  private String number;

  @Column(nullable = false)
  private boolean available = true;

  @Column(nullable = false)
  private long timesBooked = 0L;
}
