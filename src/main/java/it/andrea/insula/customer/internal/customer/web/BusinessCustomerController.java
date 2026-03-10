package it.andrea.insula.customer.internal.customer.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.customer.internal.customer.dto.request.CustomerFilters;
import it.andrea.insula.customer.internal.customer.dto.request.business.BusinessCustomerCreateDto;
import it.andrea.insula.customer.internal.customer.dto.request.business.BusinessCustomerUpdateDto;
import it.andrea.insula.customer.internal.customer.dto.request.business.CustomerContactCreateDto;
import it.andrea.insula.customer.internal.customer.dto.response.business.BusinessCustomerResponseDto;
import it.andrea.insula.customer.internal.customer.service.BusinessCustomerService;
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
@RequestMapping("/api/v1/customers/business")
@RequiredArgsConstructor
@Tag(name = "Business Customer Management", description = "APIs for managing business customers")
public class BusinessCustomerController {

    private final BusinessCustomerService service;

    @Operation(summary = "Get a business customer by Public ID")
    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.CUSTOMER_READ + "')")
    public ResponseEntity<BusinessCustomerResponseDto> getByPublicId(@PathVariable UUID publicId) {
        return ResponseEntity.ok(service.getByPublicId(publicId));
    }

    @Operation(summary = "Get all business customers (paginated)")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.CUSTOMER_READ + "')")
    public ResponseEntity<PageResponse<BusinessCustomerResponseDto>> getAll(
            @ParameterObject CustomerFilters filters,
            @ParameterObject @PageableDefault(size = 20, sort = "companyName") Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(filters, pageable));
    }

    @Operation(summary = "Get all business customers as a list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.CUSTOMER_READ + "')")
    public ResponseEntity<List<BusinessCustomerResponseDto>> getList(@ParameterObject CustomerFilters filters) {
        return ResponseEntity.ok(service.findAll(filters));
    }

    @Operation(summary = "Create a new business customer")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.CUSTOMER_CREATE + "')")
    public ResponseEntity<BusinessCustomerResponseDto> create(@Validated @RequestBody BusinessCustomerCreateDto dto) {
        BusinessCustomerResponseDto created = service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.publicId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update an existing business customer")
    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.CUSTOMER_UPDATE + "')")
    public ResponseEntity<BusinessCustomerResponseDto> update(@PathVariable UUID publicId, @Validated @RequestBody BusinessCustomerUpdateDto dto) {
        return ResponseEntity.ok(service.update(publicId, dto));
    }

    @Operation(summary = "Delete a business customer")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.CUSTOMER_DELETE + "')")
    public ResponseEntity<Void> delete(@PathVariable UUID publicId) {
        service.delete(publicId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add a contact to a business customer")
    @PostMapping("/{publicId}/contacts")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.CUSTOMER_UPDATE + "')")
    public ResponseEntity<BusinessCustomerResponseDto> addContact(@PathVariable UUID publicId, @Validated @RequestBody CustomerContactCreateDto dto) {
        return ResponseEntity.ok(service.addContact(publicId, dto));
    }

    @Operation(summary = "Remove a contact from a business customer")
    @DeleteMapping("/{publicId}/contacts/{contactPublicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.CUSTOMER_UPDATE + "')")
    public ResponseEntity<Void> removeContact(@PathVariable UUID publicId, @PathVariable UUID contactPublicId) {
        service.removeContact(publicId, contactPublicId);
        return ResponseEntity.noContent().build();
    }
}
