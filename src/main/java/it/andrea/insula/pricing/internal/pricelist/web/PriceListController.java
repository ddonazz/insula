package it.andrea.insula.pricing.internal.pricelist.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListCreateDto;
import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListPatchDto;
import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListSearchCriteria;
import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListUpdateDto;
import it.andrea.insula.pricing.internal.pricelist.dto.response.PriceListResponseDto;
import it.andrea.insula.pricing.internal.pricelist.service.PriceListService;
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
@RequestMapping("/api/v1/price-lists")
@RequiredArgsConstructor
@Tag(name = "Price Lists", description = "Manage price lists")
public class PriceListController {

    private final PriceListService service;

    @Operation(summary = "Get price list by public ID")
    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICELIST_READ + "')")
    public ResponseEntity<PriceListResponseDto> getByPublicId(@PathVariable UUID publicId) {
        return ResponseEntity.ok(service.getByPublicId(publicId));
    }

    @Operation(summary = "Get price lists (paged)")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICELIST_READ + "')")
    public ResponseEntity<PageResponse<PriceListResponseDto>> getAll(
            @ParameterObject PriceListSearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }

    @Operation(summary = "Get price lists list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICELIST_READ + "')")
    public ResponseEntity<List<PriceListResponseDto>> getList(@ParameterObject PriceListSearchCriteria criteria) {
        return ResponseEntity.ok(service.findAll(criteria));
    }

    @Operation(summary = "Create price list")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICELIST_CREATE + "')")
    public ResponseEntity<PriceListResponseDto> create(@Validated @RequestBody PriceListCreateDto dto) {
        PriceListResponseDto created = service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.publicId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update price list")
    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICELIST_UPDATE + "')")
    public ResponseEntity<PriceListResponseDto> update(@PathVariable UUID publicId, @Validated @RequestBody PriceListUpdateDto dto) {
        return ResponseEntity.ok(service.update(publicId, dto));
    }

    @Operation(summary = "Patch price list")
    @PatchMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICELIST_UPDATE + "')")
    public ResponseEntity<PriceListResponseDto> patch(@PathVariable UUID publicId, @Validated @RequestBody PriceListPatchDto dto) {
        return ResponseEntity.ok(service.patch(publicId, dto));
    }

    @Operation(summary = "Delete price list")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICELIST_DELETE + "')")
    public ResponseEntity<Void> delete(@PathVariable UUID publicId) {
        service.delete(publicId);
        return ResponseEntity.noContent().build();
    }
}

