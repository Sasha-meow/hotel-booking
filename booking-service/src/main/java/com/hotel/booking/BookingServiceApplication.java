package com.hotel.booking;

import com.hotel.common.exception.GlobalExceptionHandler;
import com.hotel.common.logging.LoggingInterceptorConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

/** Главный класс приложения Booking Service */
@SpringBootApplication
@EnableFeignClients
@EnableRetry
@Import({GlobalExceptionHandler.class, LoggingInterceptorConfig.class})
public class BookingServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(BookingServiceApplication.class, args);
  }
}
