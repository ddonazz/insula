package it.andrea.insula.property.internal.room.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.property.internal.room.dto.request.RoomCreateDto;
import it.andrea.insula.property.internal.room.dto.request.RoomPatchDto;
import it.andrea.insula.property.internal.room.dto.response.RoomResponseDto;
import it.andrea.insula.property.internal.room.service.RoomService;
import it.andrea.insula.security.PermissionAuthority;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties/{propertyId}/units/{unitId}/rooms")
@RequiredArgsConstructor
@Tag(name = "Room Management", description = "APIs for managing rooms within a unit")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Get a room by Public ID")
    @GetMapping("/{roomId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.UNIT_READ + "')")
    public ResponseEntity<RoomResponseDto> getByPublicId(
            @PathVariable UUID propertyId,
            @PathVariable UUID unitId,
            @PathVariable UUID roomId
    ) {
        return ResponseEntity.ok(roomService.getByPublicId(propertyId, unitId, roomId));
    }

    @Operation(summary = "Get all rooms for a unit")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.UNIT_READ + "')")
    public ResponseEntity<List<RoomResponseDto>> getAll(
            @PathVariable UUID propertyId,
            @PathVariable UUID unitId
    ) {
        return ResponseEntity.ok(roomService.findAllByUnit(propertyId, unitId));
    }

    @Operation(summary = "Create a new room within a unit")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.UNIT_CREATE + "')")
    public ResponseEntity<RoomResponseDto> create(
            @PathVariable UUID propertyId,
            @PathVariable UUID unitId,
            @Validated @RequestBody RoomCreateDto dto
    ) {
        RoomResponseDto created = roomService.create(propertyId, unitId, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{roomId}")
                .buildAndExpand(created.publicId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update an existing room")
    @PutMapping("/{roomId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.UNIT_UPDATE + "')")
    public ResponseEntity<RoomResponseDto> update(
            @PathVariable UUID propertyId,
            @PathVariable UUID unitId,
            @PathVariable UUID roomId,
            @Validated @RequestBody RoomPatchDto dto
    ) {
        return ResponseEntity.ok(roomService.update(propertyId, unitId, roomId, dto));
    }

    @Operation(summary = "Delete a room")
    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.UNIT_DELETE + "')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID propertyId,
            @PathVariable UUID unitId,
            @PathVariable UUID roomId
    ) {
        roomService.delete(propertyId, unitId, roomId);
        return ResponseEntity.noContent().build();
    }
}

