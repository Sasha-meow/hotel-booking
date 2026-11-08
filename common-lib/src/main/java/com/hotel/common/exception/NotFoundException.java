package com.hotel.common.exception;

/** Исключение: ресурс не найден (404) */
public class NotFoundException extends RuntimeException {

  public NotFoundException(String message) {
    super(message);
  }
}
