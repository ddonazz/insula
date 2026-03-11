package it.andrea.insula.property.internal.room.service;

import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.property.internal.property.exception.PropertyErrorCodes;
import it.andrea.insula.property.internal.room.dto.request.RoomCreateDto;
import it.andrea.insula.property.internal.room.dto.request.RoomPatchDto;
import it.andrea.insula.property.internal.room.dto.request.RoomUpdateDto;
import it.andrea.insula.property.internal.room.dto.response.RoomResponseDto;
import it.andrea.insula.property.internal.room.mapper.RoomCreateMapper;
import it.andrea.insula.property.internal.room.mapper.RoomPatchMapper;
import it.andrea.insula.property.internal.room.mapper.RoomResponseMapper;
import it.andrea.insula.property.internal.room.mapper.RoomUpdateMapper;
import it.andrea.insula.property.internal.room.model.Room;
import it.andrea.insula.property.internal.room.model.RoomRepository;
import it.andrea.insula.property.internal.unit.model.Unit;
import it.andrea.insula.property.internal.unit.model.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final UnitRepository unitRepository;
    private final RoomCreateMapper createMapper;
    private final RoomUpdateMapper updateMapper;
    private final RoomPatchMapper patchMapper;
    private final RoomResponseMapper responseMapper;

    @Transactional
    public RoomResponseDto create(UUID propertyPublicId, UUID unitPublicId, RoomCreateDto dto) {
        Unit unit = unitRepository.findByPublicIdAndPropertyPublicId(unitPublicId, propertyPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.UNIT_NOT_FOUND, unitPublicId));

        Room room = createMapper.apply(dto);
        room.setUnit(unit);
        Room saved = roomRepository.save(room);
        return responseMapper.apply(saved);
    }

    @Transactional
    public RoomResponseDto update(UUID propertyPublicId, UUID unitPublicId, UUID roomPublicId, RoomUpdateDto dto) {
        unitRepository.findByPublicIdAndPropertyPublicId(unitPublicId, propertyPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.UNIT_NOT_FOUND, unitPublicId));

        Room room = roomRepository.findByPublicIdAndUnitPublicId(roomPublicId, unitPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.ROOM_NOT_FOUND, roomPublicId));

        updateMapper.apply(dto, room);
        Room updated = roomRepository.save(room);
        return responseMapper.apply(updated);
    }

    @Transactional
    public RoomResponseDto patch(UUID propertyPublicId, UUID unitPublicId, UUID roomPublicId, RoomPatchDto dto) {
        unitRepository.findByPublicIdAndPropertyPublicId(unitPublicId, propertyPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.UNIT_NOT_FOUND, unitPublicId));

        Room room = roomRepository.findByPublicIdAndUnitPublicId(roomPublicId, unitPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.ROOM_NOT_FOUND, roomPublicId));

        patchMapper.apply(dto, room);
        Room updated = roomRepository.save(room);
        return responseMapper.apply(updated);
    }

    public RoomResponseDto getByPublicId(UUID propertyPublicId, UUID unitPublicId, UUID roomPublicId) {
        unitRepository.findByPublicIdAndPropertyPublicId(unitPublicId, propertyPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.UNIT_NOT_FOUND, unitPublicId));

        return roomRepository.findByPublicIdAndUnitPublicId(roomPublicId, unitPublicId)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.ROOM_NOT_FOUND, roomPublicId));
    }

    public List<RoomResponseDto> findAllByUnit(UUID propertyPublicId, UUID unitPublicId) {
        unitRepository.findByPublicIdAndPropertyPublicId(unitPublicId, propertyPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.UNIT_NOT_FOUND, unitPublicId));

        return roomRepository.findAllByUnitPublicId(unitPublicId).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID propertyPublicId, UUID unitPublicId, UUID roomPublicId) {
        unitRepository.findByPublicIdAndPropertyPublicId(unitPublicId, propertyPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.UNIT_NOT_FOUND, unitPublicId));

        Room room = roomRepository.findByPublicIdAndUnitPublicId(roomPublicId, unitPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.ROOM_NOT_FOUND, roomPublicId));
        roomRepository.delete(room);
    }
}
