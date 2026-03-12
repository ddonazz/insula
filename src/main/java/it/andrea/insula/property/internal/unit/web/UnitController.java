package it.andrea.insula.property.internal.unit.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.property.internal.unit.dto.request.CadastralDataPatchDto;
import it.andrea.insula.property.internal.unit.dto.request.UnitCreateDto;
import it.andrea.insula.property.internal.unit.dto.request.UnitPatchDto;
import it.andrea.insula.property.internal.unit.dto.request.UnitUpdateDto;
import it.andrea.insula.property.internal.unit.dto.response.CadastralDataResponseDto;
import it.andrea.insula.property.internal.unit.dto.response.UnitResponseDto;
import it.andrea.insula.property.internal.unit.service.UnitService;
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
@RequestMapping("/api/v1/properties/{propertyId}/units")
@RequiredArgsConstructor
@Tag(name = "Unit Management", description = "APIs for managing units within a property")
public class UnitController {

    private final UnitService unitService;

    @Operation(summary = "Get a unit by Public ID")
    @GetMapping("/{unitId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.UNIT_READ + "')")
    public ResponseEntity<UnitResponseDto> getByPublicId(
            @PathVariable UUID propertyId,
            @PathVariable UUID unitId
    ) {
        return ResponseEntity.ok(unitService.getByPublicId(propertyId, unitId));
    }

    @Operation(summary = "Get all units for a property")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.UNIT_READ + "')")
    public ResponseEntity<List<UnitResponseDto>> getAll(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(unitService.findAllByProperty(propertyId));
    }

    @Operation(summary = "Create a new unit within a property")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.UNIT_CREATE + "')")
    public ResponseEntity<UnitResponseDto> create(
            @PathVariable UUID propertyId,
            @Validated @RequestBody UnitCreateDto dto
    ) {
        UnitResponseDto created = unitService.create(propertyId, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{unitId}")
                .buildAndExpand(created.publicId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update an existing unit")
    @PutMapping("/{unitId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.UNIT_UPDATE + "')")
    public ResponseEntity<UnitResponseDto> update(
            @PathVariable UUID propertyId,
            @PathVariable UUID unitId,
            @Validated @RequestBody UnitUpdateDto dto
    ) {
        return ResponseEntity.ok(unitService.update(propertyId, unitId, dto));
    }

    @Operation(summary = "Patch an existing unit")
    @PatchMapping("/{unitId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.UNIT_UPDATE + "')")
    public ResponseEntity<UnitResponseDto> patch(
            @PathVariable UUID propertyId,
            @PathVariable UUID unitId,
            @Validated @RequestBody UnitPatchDto dto
    ) {
        return ResponseEntity.ok(unitService.patch(propertyId, unitId, dto));
    }

    @Operation(summary = "Delete a unit")
    @DeleteMapping("/{unitId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.UNIT_DELETE + "')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID propertyId,
            @PathVariable UUID unitId
    ) {
        unitService.delete(propertyId, unitId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Patch cadastral data of a unit")
    @PatchMapping("/{unitId}/cadastral-data/{cadastralDataId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.UNIT_UPDATE + "')")
    public ResponseEntity<CadastralDataResponseDto> patchCadastralData(
            @PathVariable UUID propertyId,
            @PathVariable UUID unitId,
            @PathVariable UUID cadastralDataId,
            @Validated @RequestBody CadastralDataPatchDto dto
    ) {
        return ResponseEntity.ok(unitService.patchCadastralData(propertyId, unitId, cadastralDataId, dto));
    }
}

