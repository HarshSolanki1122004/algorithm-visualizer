package com.harsh.visualizer.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Consistent error payload for all handled exceptions.
 *
 * @param timestamp   when the error occurred
 * @param status      HTTP status code
 * @param error       short reason phrase
 * @param message     human-readable detail
 * @param path        request URI
 * @param fieldErrors per-field validation messages (empty unless a validation error)
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
}
