package it.andrea.insula.user.internal.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
