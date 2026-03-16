package it.andrea.insula.pricing.internal.rate.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.pricing.internal.rate.dto.request.RateCreateDto;
import it.andrea.insula.pricing.internal.rate.dto.request.RatePatchDto;
import it.andrea.insula.pricing.internal.rate.dto.request.RateSearchCriteria;
import it.andrea.insula.pricing.internal.rate.dto.request.RateUpdateDto;
import it.andrea.insula.pricing.internal.rate.dto.response.RateResponseDto;
import it.andrea.insula.pricing.internal.rate.service.RateService;
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
@RequestMapping("/api/v1/price-lists/{priceListPublicId}/rates")
@RequiredArgsConstructor
@Tag(name = "Rates", description = "Manage daily unit rates")
public class RateController {

    private final RateService service;

    @Operation(summary = "Get rate by public ID")
    @GetMapping("/{ratePublicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_READ + "')")
    public ResponseEntity<RateResponseDto> getByPublicId(
            @PathVariable UUID priceListPublicId,
            @PathVariable UUID ratePublicId
    ) {
        return ResponseEntity.ok(service.getByPublicId(priceListPublicId, ratePublicId));
    }

    @Operation(summary = "Get rates (paged)")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_READ + "')")
    public ResponseEntity<PageResponse<RateResponseDto>> getAll(
            @PathVariable UUID priceListPublicId,
            @ParameterObject RateSearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "stayDate") Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(priceListPublicId, criteria, pageable));
    }

    @Operation(summary = "Get rates list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_READ + "')")
    public ResponseEntity<List<RateResponseDto>> getList(
            @PathVariable UUID priceListPublicId,
            @ParameterObject RateSearchCriteria criteria
    ) {
        return ResponseEntity.ok(service.findAll(priceListPublicId, criteria));
    }

    @Operation(summary = "Create rate")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_CREATE + "')")
    public ResponseEntity<RateResponseDto> create(
            @PathVariable UUID priceListPublicId,
            @Validated @RequestBody RateCreateDto dto
    ) {
        RateResponseDto created = service.create(priceListPublicId, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{ratePublicId}")
                .buildAndExpand(created.publicId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update rate")
    @PutMapping("/{ratePublicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_UPDATE + "')")
    public ResponseEntity<RateResponseDto> update(
            @PathVariable UUID priceListPublicId,
            @PathVariable UUID ratePublicId,
            @Validated @RequestBody RateUpdateDto dto
    ) {
        return ResponseEntity.ok(service.update(priceListPublicId, ratePublicId, dto));
    }

    @Operation(summary = "Patch rate")
    @PatchMapping("/{ratePublicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_UPDATE + "')")
    public ResponseEntity<RateResponseDto> patch(
            @PathVariable UUID priceListPublicId,
            @PathVariable UUID ratePublicId,
            @Validated @RequestBody RatePatchDto dto
    ) {
        return ResponseEntity.ok(service.patch(priceListPublicId, ratePublicId, dto));
    }

    @Operation(summary = "Delete rate")
    @DeleteMapping("/{ratePublicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_DELETE + "')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID priceListPublicId,
            @PathVariable UUID ratePublicId
    ) {
        service.delete(priceListPublicId, ratePublicId);
        return ResponseEntity.noContent().build();
    }
}

