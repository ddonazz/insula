package it.andrea.insula.property.internal.room.dto.request;

import it.andrea.insula.property.internal.room.model.RoomType;

import java.util.Set;

public record RoomPatchDto(
        RoomType type,

        Double areaMq,

        Set<String> features
) {
}

