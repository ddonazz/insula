package it.andrea.insula.property.internal.room.mapper;

import it.andrea.insula.property.internal.room.dto.request.RoomUpdateDto;
import it.andrea.insula.property.internal.room.model.Room;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.BiFunction;

@Component
public class RoomUpdateMapper implements BiFunction<RoomUpdateDto, Room, Room> {

    @Override
    public Room apply(RoomUpdateDto dto, Room room) {
        room.setType(dto.type());
        room.setAreaMq(dto.areaMq());
        room.setFeatures(dto.features() != null ? new HashSet<>(dto.features()) : new HashSet<>());
        return room;
    }
}

