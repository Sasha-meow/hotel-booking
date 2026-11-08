package com.hotel.booking.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.client.HotelClient;
import com.hotel.booking.dto.BookingCreateDto;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.security.JwtService;
import com.hotel.common.dto.AvailabilityRequest;
import com.hotel.common.dto.AvailabilityResponse;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private BookingRepository bookingRepository;

  @Autowired private JwtService jwtService;

  @MockBean private HotelClient hotelClient;

  private String userToken;

  @BeforeEach
  void setUp() {
    bookingRepository.deleteAll();
    reset(hotelClient);
    // Генерируем реальный токен для пользователя с ID = 1
    userToken = "Bearer " + jwtService.generateToken(1L, "testuser", "USER");
  }

  @Test
  void shouldReturnUnauthorized_WhenNoToken() throws Exception {
    BookingCreateDto dto =
        new BookingCreateDto(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), false);

    mockMvc
        .perform(
            post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturnBadRequest_WhenEndDateBeforeStartDate() throws Exception {
    BookingCreateDto dto =
        new BookingCreateDto(1L, LocalDate.now().plusDays(5), LocalDate.now().plusDays(1), false);

    mockMvc
        .perform(
            post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", userToken)
                .header("Idempotency-Key", "test-validation-key"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnCreatedAndConfirmed_WhenHotelServiceConfirms() throws Exception {
    AvailabilityResponse mockResponse =
        new AvailabilityResponse(true, 1L, "req-success", "Confirmed");
    when(hotelClient.confirmAvailability(anyLong(), any(AvailabilityRequest.class)))
        .thenReturn(mockResponse);

    BookingCreateDto dto =
        new BookingCreateDto(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), false);

    mockMvc
        .perform(
            post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", userToken)
                .header("Idempotency-Key", "test-success-key"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("CONFIRMED"));

    verify(hotelClient, times(1)).confirmAvailability(anyLong(), any());
  }

  @Test
  void shouldReturnCreatedAndCancelled_WhenHotelServiceFails() throws Exception {
    when(hotelClient.confirmAvailability(anyLong(), any(AvailabilityRequest.class)))
        .thenThrow(new RuntimeException("Hotel Service is down"));

    BookingCreateDto dto =
        new BookingCreateDto(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), false);

    mockMvc
        .perform(
            post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", userToken)
                .header("Idempotency-Key", "test-compensation-key"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("CANCELLED"));

    verify(hotelClient, atLeastOnce()).release(anyLong(), anyString());
  }

  @Test
  void shouldReturnSameBooking_WhenSameIdempotencyKeyUsed() throws Exception {
    AvailabilityResponse mockResponse =
        new AvailabilityResponse(true, 1L, "req-idemp", "Confirmed");
    when(hotelClient.confirmAvailability(anyLong(), any(AvailabilityRequest.class)))
        .thenReturn(mockResponse);

    BookingCreateDto dto =
        new BookingCreateDto(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), false);
    String json = objectMapper.writeValueAsString(dto);
    String idempotencyKey = "test-idempotency-123";

    String response1 =
        mockMvc
            .perform(
                post("/api/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .header("Authorization", userToken)
                    .header("Idempotency-Key", idempotencyKey))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String response2 =
        mockMvc
            .perform(
                post("/api/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
                    .header("Authorization", userToken)
                    .header("Idempotency-Key", idempotencyKey))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String id1 = objectMapper.readTree(response1).get("id").asText();
    String id2 = objectMapper.readTree(response2).get("id").asText();

    assertEquals(id1, id2);
    assertEquals(1, bookingRepository.count());
  }
}
