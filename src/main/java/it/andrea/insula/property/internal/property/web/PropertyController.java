package it.andrea.insula.property.internal.property.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.property.internal.property.dto.request.PropertyCreateDto;
import it.andrea.insula.property.internal.property.dto.request.PropertyPatchDto;
import it.andrea.insula.property.internal.property.dto.request.PropertySearchCriteria;
import it.andrea.insula.property.internal.property.dto.response.PropertyResponseDto;
import it.andrea.insula.property.internal.property.service.PropertyService;
import it.andrea.insula.security.PermissionAuthority;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
@Tag(name = "Property Management", description = "APIs for managing properties")
public class PropertyController {

    private final PropertyService propertyService;

    @Operation(summary = "Get a property by Public ID (UUID)")
    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PROPERTY_READ + "')")
    public ResponseEntity<PropertyResponseDto> getByPublicId(@PathVariable UUID publicId) {
        return ResponseEntity.ok(propertyService.getByPublicId(publicId));
    }

    @Operation(summary = "Get all properties (paginated)")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PROPERTY_READ + "')")
    public ResponseEntity<PageResponse<PropertyResponseDto>> getAll(
            @ParameterObject PropertySearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        return ResponseEntity.ok(propertyService.getAll(criteria, pageable));
    }

    @Operation(summary = "Get all properties as a list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PROPERTY_READ + "')")
    public ResponseEntity<List<PropertyResponseDto>> getList(@ParameterObject PropertySearchCriteria criteria) {
        return ResponseEntity.ok(propertyService.findAll(criteria));
    }

    @Operation(summary = "Create a new property")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PROPERTY_CREATE + "')")
    public ResponseEntity<PropertyResponseDto> create(@Validated @RequestBody PropertyCreateDto dto) {
        PropertyResponseDto created = propertyService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.publicId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update an existing property")
    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PROPERTY_UPDATE + "')")
    public ResponseEntity<PropertyResponseDto> update(@PathVariable UUID publicId, @Validated @RequestBody PropertyPatchDto dto) {
        return ResponseEntity.ok(propertyService.update(publicId, dto));
    }

    @Operation(summary = "Delete a property")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PROPERTY_DELETE + "')")
    public ResponseEntity<Void> delete(@PathVariable UUID publicId) {
        propertyService.delete(publicId);
        return ResponseEntity.noContent().build();
    }
}

