package it.andrea.insula.pricing.internal.promotion.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.pricing.internal.promotion.dto.request.PromotionCreateDto;
import it.andrea.insula.pricing.internal.promotion.dto.request.PromotionPatchDto;
import it.andrea.insula.pricing.internal.promotion.dto.request.PromotionSearchCriteria;
import it.andrea.insula.pricing.internal.promotion.dto.request.PromotionUpdateDto;
import it.andrea.insula.pricing.internal.promotion.dto.response.PromotionResponseDto;
import it.andrea.insula.pricing.internal.promotion.service.PromotionService;
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
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotions", description = "Manage promotions")
public class PromotionController {

    private final PromotionService service;

    @Operation(summary = "Get promotion by public ID")
    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PROMOTION_READ + "')")
    public ResponseEntity<PromotionResponseDto> getByPublicId(@PathVariable UUID publicId) {
        return ResponseEntity.ok(service.getByPublicId(publicId));
    }

    @Operation(summary = "Get promotions (paged)")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PROMOTION_READ + "')")
    public ResponseEntity<PageResponse<PromotionResponseDto>> getAll(
            @ParameterObject PromotionSearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "bookingFrom") Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }

    @Operation(summary = "Get promotions list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PROMOTION_READ + "')")
    public ResponseEntity<List<PromotionResponseDto>> getList(@ParameterObject PromotionSearchCriteria criteria) {
        return ResponseEntity.ok(service.findAll(criteria));
    }

    @Operation(summary = "Create promotion")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PROMOTION_CREATE + "')")
    public ResponseEntity<PromotionResponseDto> create(@Validated @RequestBody PromotionCreateDto dto) {
        PromotionResponseDto created = service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.publicId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update promotion")
    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PROMOTION_UPDATE + "')")
    public ResponseEntity<PromotionResponseDto> update(@PathVariable UUID publicId, @Validated @RequestBody PromotionUpdateDto dto) {
        return ResponseEntity.ok(service.update(publicId, dto));
    }

    @Operation(summary = "Patch promotion")
    @PatchMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PROMOTION_UPDATE + "')")
    public ResponseEntity<PromotionResponseDto> patch(@PathVariable UUID publicId, @Validated @RequestBody PromotionPatchDto dto) {
        return ResponseEntity.ok(service.patch(publicId, dto));
    }

    @Operation(summary = "Delete promotion")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PROMOTION_DELETE + "')")
    public ResponseEntity<Void> delete(@PathVariable UUID publicId) {
        service.delete(publicId);
        return ResponseEntity.noContent().build();
    }
}

