package com.hotel.hotel;

import com.hotel.common.exception.GlobalExceptionHandler;
import com.hotel.common.logging.LoggingInterceptorConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/** Главный класс приложения Hotel Service */
@SpringBootApplication
@Import({GlobalExceptionHandler.class, LoggingInterceptorConfig.class})
public class HotelServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(HotelServiceApplication.class, args);
  }
}
