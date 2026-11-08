package com.hotel.hotel.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.common.dto.AvailabilityRequest;
import com.hotel.hotel.dto.HotelCreateDto;
import com.hotel.hotel.entity.Hotel;
import com.hotel.hotel.entity.Room;
import com.hotel.hotel.repository.HotelRepository;
import com.hotel.hotel.repository.RoomBlockRepository;
import com.hotel.hotel.repository.RoomRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HotelControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private HotelRepository hotelRepository;

  @Autowired private RoomRepository roomRepository;

  @Autowired private RoomBlockRepository roomBlockRepository;

  @Value("${security.jwt.secret}")
  private String secret;

  private String adminToken;
  private Long roomId;

  @BeforeEach
  void setUp() {
    roomBlockRepository.deleteAll();
    roomRepository.deleteAll();
    hotelRepository.deleteAll();

    adminToken = "Bearer " + generateTestToken(1L, "admin", "ADMIN");

    Hotel hotel = Hotel.builder().name("Test Hotel").address("Test Address").build();
    hotel = hotelRepository.save(hotel);

    Room room = Room.builder().hotel(hotel).number("101").available(true).timesBooked(0L).build();
    room = roomRepository.save(room);
    roomId = room.getId();
  }

  private String generateTestToken(Long userId, String username, String role) {
    HashMap<String, Object> claims = new HashMap<>();
    claims.put("userId", userId);
    claims.put("role", role);

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 3600000))
        .signWith(
            Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
        .compact();
  }

  @Test
  void shouldReturnUnauthorized_WhenNoToken() throws Exception {
    HotelCreateDto dto = new HotelCreateDto("Test Hotel", "Address");

    mockMvc
        .perform(
            post("/api/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
    HotelCreateDto dto = new HotelCreateDto("", "Address");

    mockMvc
        .perform(
            post("/api/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", adminToken))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnCreated_WhenValidHotel() throws Exception {
    HotelCreateDto dto = new HotelCreateDto("New Hotel", "New Address");

    mockMvc
        .perform(
            post("/api/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", adminToken))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldReturnOk_WhenConfirmAvailability() throws Exception {
    AvailabilityRequest request =
        new AvailabilityRequest(
            LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), "test-req-1");

    mockMvc
        .perform(
            post("/api/rooms/" + roomId + "/confirm-availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", adminToken))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturnConflict_WhenDatesOverlap() throws Exception {
    AvailabilityRequest request =
        new AvailabilityRequest(
            LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), "test-req-2");

    mockMvc.perform(
        post("/api/rooms/" + roomId + "/confirm-availability")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("Authorization", adminToken));

    AvailabilityRequest overlapRequest =
        new AvailabilityRequest(
            LocalDate.now().plusDays(3), LocalDate.now().plusDays(7), "test-req-3");

    mockMvc
        .perform(
            post("/api/rooms/" + roomId + "/confirm-availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlapRequest))
                .header("Authorization", adminToken))
        .andExpect(status().isConflict());
  }
}
