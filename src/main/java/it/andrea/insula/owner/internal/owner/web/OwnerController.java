package it.andrea.insula.owner.internal.owner.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerCreateDto;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerPatchDto;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerSearchCriteria;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerUpdateDto;
import it.andrea.insula.owner.internal.owner.dto.response.OwnerResponseDto;
import it.andrea.insula.owner.internal.owner.service.OwnerService;
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
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
@Tag(name = "Owner Management", description = "Unified APIs for managing property owners (individual and business)")
public class OwnerController {

    private final OwnerService service;

    @Operation(summary = "Get an owner by Public ID")
    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.OWNER_READ + "')")
    public ResponseEntity<OwnerResponseDto> getByPublicId(@PathVariable UUID publicId) {
        return ResponseEntity.ok(service.getByPublicId(publicId));
    }

    @Operation(summary = "Get all owners (paginated)")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.OWNER_READ + "')")
    public ResponseEntity<PageResponse<OwnerResponseDto>> getAll(
            @ParameterObject OwnerSearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "email") Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }

    @Operation(summary = "Get all owners as a list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.OWNER_READ + "')")
    public ResponseEntity<List<OwnerResponseDto>> getList(@ParameterObject OwnerSearchCriteria criteria) {
        return ResponseEntity.ok(service.findAll(criteria));
    }

    @Operation(summary = "Create a new owner (individual or business)")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.OWNER_CREATE + "')")
    public ResponseEntity<OwnerResponseDto> create(@Validated @RequestBody OwnerCreateDto dto) {
        OwnerResponseDto created = service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.publicId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Full update an existing owner")
    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.OWNER_UPDATE + "')")
    public ResponseEntity<OwnerResponseDto> update(
            @PathVariable UUID publicId,
            @Validated @RequestBody OwnerUpdateDto dto
    ) {
        return ResponseEntity.ok(service.update(publicId, dto));
    }

    @Operation(summary = "Partial update an existing owner")
    @PatchMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.OWNER_UPDATE + "')")
    public ResponseEntity<OwnerResponseDto> patch(
            @PathVariable UUID publicId,
            @Validated @RequestBody OwnerPatchDto dto
    ) {
        return ResponseEntity.ok(service.patch(publicId, dto));
    }

    @Operation(summary = "Delete an owner (soft delete)")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.OWNER_DELETE + "')")
    public ResponseEntity<Void> delete(@PathVariable UUID publicId) {
        service.delete(publicId);
        return ResponseEntity.noContent().build();
    }
}

