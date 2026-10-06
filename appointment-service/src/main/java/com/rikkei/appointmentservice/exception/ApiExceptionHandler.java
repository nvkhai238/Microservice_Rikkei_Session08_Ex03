package com.rikkei.appointmentservice.exception;

import com.rikkei.appointmentservice.dto.ApiResponseError;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponseError> handleStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity.status(status).body(ApiResponseError.of(status.value(), ex.getReason()));
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ApiResponseError> handleOpenCircuit(CallNotPermittedException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseError.of(503, "Doctor-Service tạm thời không sẵn sàng."));
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ApiResponseError> handleRateLimit(RequestNotPermitted ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponseError.of(429, "Bạn đã gọi quá nhiều lần, vui lòng thử lại sau."));
    }
}
