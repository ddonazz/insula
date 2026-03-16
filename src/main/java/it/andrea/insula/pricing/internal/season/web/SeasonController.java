package it.andrea.insula.pricing.internal.season.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.pricing.internal.season.dto.request.SeasonCreateDto;
import it.andrea.insula.pricing.internal.season.dto.request.SeasonPatchDto;
import it.andrea.insula.pricing.internal.season.dto.request.SeasonSearchCriteria;
import it.andrea.insula.pricing.internal.season.dto.request.SeasonUpdateDto;
import it.andrea.insula.pricing.internal.season.dto.response.SeasonResponseDto;
import it.andrea.insula.pricing.internal.season.service.SeasonService;
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
@RequestMapping("/api/v1/price-lists/{priceListPublicId}/seasons")
@RequiredArgsConstructor
@Tag(name = "Seasons", description = "Manage seasons")
public class SeasonController {

    private final SeasonService service;

    @Operation(summary = "Get season by public ID")
    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.SEASON_READ + "')")
    public ResponseEntity<SeasonResponseDto> getByPublicId(@PathVariable UUID priceListPublicId, @PathVariable UUID publicId) {
        return ResponseEntity.ok(service.getByPublicId(priceListPublicId, publicId));
    }

    @Operation(summary = "Get seasons (paged)")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.SEASON_READ + "')")
    public ResponseEntity<PageResponse<SeasonResponseDto>> getAll(
            @PathVariable UUID priceListPublicId,
            @ParameterObject SeasonSearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "startDate") Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(priceListPublicId, criteria, pageable));
    }

    @Operation(summary = "Get seasons list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.SEASON_READ + "')")
    public ResponseEntity<List<SeasonResponseDto>> getList(
            @PathVariable UUID priceListPublicId,
            @ParameterObject SeasonSearchCriteria criteria
    ) {
        return ResponseEntity.ok(service.findAll(priceListPublicId, criteria));
    }

    @Operation(summary = "Create season")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.SEASON_CREATE + "')")
    public ResponseEntity<SeasonResponseDto> create(
            @PathVariable UUID priceListPublicId,
            @Validated @RequestBody SeasonCreateDto dto
    ) {
        SeasonResponseDto created = service.create(priceListPublicId, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.publicId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update season")
    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.SEASON_UPDATE + "')")
    public ResponseEntity<SeasonResponseDto> update(
            @PathVariable UUID priceListPublicId,
            @PathVariable UUID publicId,
            @Validated @RequestBody SeasonUpdateDto dto
    ) {
        return ResponseEntity.ok(service.update(priceListPublicId, publicId, dto));
    }

    @Operation(summary = "Patch season")
    @PatchMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.SEASON_UPDATE + "')")
    public ResponseEntity<SeasonResponseDto> patch(
            @PathVariable UUID priceListPublicId,
            @PathVariable UUID publicId,
            @Validated @RequestBody SeasonPatchDto dto
    ) {
        return ResponseEntity.ok(service.patch(priceListPublicId, publicId, dto));
    }

    @Operation(summary = "Delete season")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.SEASON_DELETE + "')")
    public ResponseEntity<Void> delete(@PathVariable UUID priceListPublicId, @PathVariable UUID publicId) {
        service.delete(priceListPublicId, publicId);
        return ResponseEntity.noContent().build();
    }
}

