package com.philosophia.exceptions;

public class InvalidTeachingPlanException extends RuntimeException {
  public InvalidTeachingPlanException(String message) {
    super(message);
  }
}