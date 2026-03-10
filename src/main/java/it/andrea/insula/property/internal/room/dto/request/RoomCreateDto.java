package it.andrea.insula.property.internal.room.dto.request;

import it.andrea.insula.property.internal.room.model.RoomType;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RoomCreateDto(
        @NotNull
        RoomType type,

        Double areaMq,

        Set<String> features
) {
}

