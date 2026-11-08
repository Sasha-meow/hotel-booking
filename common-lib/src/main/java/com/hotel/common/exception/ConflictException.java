package com.hotel.common.exception;

/** Исключение: конфликт данных (409) */
public class ConflictException extends RuntimeException {

  public ConflictException(String message) {
    super(message);
  }
}
