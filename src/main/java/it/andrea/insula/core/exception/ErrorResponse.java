package it.andrea.insula.core.exception;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponse(
        Instant instant,
        int status,
        String error,
        String message,
        String path,
        int appCode
) {
}