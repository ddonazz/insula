package it.andrea.insula.property.internal.room.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record RoomResponseDto(
        UUID publicId,
        UUID unitPublicId,
        TranslatedEnum type,
        Double areaMq,
        Set<String> features
) {
}

