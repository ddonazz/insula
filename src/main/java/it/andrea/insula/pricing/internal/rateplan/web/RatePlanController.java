package it.andrea.insula.pricing.internal.rateplan.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.pricing.internal.rateplan.dto.request.RatePlanCreateDto;
import it.andrea.insula.pricing.internal.rateplan.dto.request.RatePlanPatchDto;
import it.andrea.insula.pricing.internal.rateplan.dto.request.RatePlanSearchCriteria;
import it.andrea.insula.pricing.internal.rateplan.dto.request.RatePlanUpdateDto;
import it.andrea.insula.pricing.internal.rateplan.dto.response.RatePlanResponseDto;
import it.andrea.insula.pricing.internal.rateplan.service.RatePlanService;
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
@RequestMapping("/api/v1/price-lists/{priceListPublicId}/rate-plans")
@RequiredArgsConstructor
@Tag(name = "Rate Plans", description = "Manage rate plans")
public class RatePlanController {

    private final RatePlanService service;

    @Operation(summary = "Get rate plan by public ID")
    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATEPLAN_READ + "')")
    public ResponseEntity<RatePlanResponseDto> getByPublicId(@PathVariable UUID priceListPublicId, @PathVariable UUID publicId) {
        return ResponseEntity.ok(service.getByPublicId(priceListPublicId, publicId));
    }

    @Operation(summary = "Get rate plans (paged)")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATEPLAN_READ + "')")
    public ResponseEntity<PageResponse<RatePlanResponseDto>> getAll(
            @PathVariable UUID priceListPublicId,
            @ParameterObject RatePlanSearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(priceListPublicId, criteria, pageable));
    }

    @Operation(summary = "Get rate plans list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATEPLAN_READ + "')")
    public ResponseEntity<List<RatePlanResponseDto>> getList(
            @PathVariable UUID priceListPublicId,
            @ParameterObject RatePlanSearchCriteria criteria
    ) {
        return ResponseEntity.ok(service.findAll(priceListPublicId, criteria));
    }

    @Operation(summary = "Create rate plan")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATEPLAN_CREATE + "')")
    public ResponseEntity<RatePlanResponseDto> create(
            @PathVariable UUID priceListPublicId,
            @Validated @RequestBody RatePlanCreateDto dto
    ) {
        RatePlanResponseDto created = service.create(priceListPublicId, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.publicId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update rate plan")
    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATEPLAN_UPDATE + "')")
    public ResponseEntity<RatePlanResponseDto> update(
            @PathVariable UUID priceListPublicId,
            @PathVariable UUID publicId,
            @Validated @RequestBody RatePlanUpdateDto dto
    ) {
        return ResponseEntity.ok(service.update(priceListPublicId, publicId, dto));
    }

    @Operation(summary = "Patch rate plan")
    @PatchMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATEPLAN_UPDATE + "')")
    public ResponseEntity<RatePlanResponseDto> patch(
            @PathVariable UUID priceListPublicId,
            @PathVariable UUID publicId,
            @Validated @RequestBody RatePlanPatchDto dto
    ) {
        return ResponseEntity.ok(service.patch(priceListPublicId, publicId, dto));
    }

    @Operation(summary = "Delete rate plan")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATEPLAN_DELETE + "')")
    public ResponseEntity<Void> delete(@PathVariable UUID priceListPublicId, @PathVariable UUID publicId) {
        service.delete(priceListPublicId, publicId);
        return ResponseEntity.noContent().build();
    }
}

