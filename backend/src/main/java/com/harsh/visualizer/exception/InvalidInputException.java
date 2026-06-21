package com.harsh.visualizer.exception;

/**
 * Thrown when a request is structurally valid but semantically rejected
 * (e.g. array too large, or binary search given an unsorted array).
 * Mapped to HTTP 400 by {@link GlobalExceptionHandler}.
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
