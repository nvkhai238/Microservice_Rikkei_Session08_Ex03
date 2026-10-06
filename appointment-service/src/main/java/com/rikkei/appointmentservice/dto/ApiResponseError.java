package com.rikkei.appointmentservice.dto;

import java.time.Instant;

public record ApiResponseError(int status, String message, Instant timestamp) {
    public static ApiResponseError of(int status, String message) {
        return new ApiResponseError(status, message, Instant.now());
    }
}
