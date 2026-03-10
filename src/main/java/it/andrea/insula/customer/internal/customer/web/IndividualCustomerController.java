package it.andrea.insula.customer.internal.customer.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.customer.internal.customer.dto.request.CustomerFilters;
import it.andrea.insula.customer.internal.customer.dto.request.individual.IndividualCustomerCreateDto;
import it.andrea.insula.customer.internal.customer.dto.request.individual.IndividualCustomerUpdateDto;
import it.andrea.insula.customer.internal.customer.dto.response.individual.IndividualCustomerResponseDto;
import it.andrea.insula.customer.internal.customer.service.IndividualCustomerService;
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
@RequestMapping("/api/v1/customers/individual")
@RequiredArgsConstructor
@Tag(name = "Individual Customer Management", description = "APIs for managing individual (private) customers")
public class IndividualCustomerController {

    private final IndividualCustomerService service;

    @Operation(summary = "Get an individual customer by Public ID")
    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('customer:read')")
    public ResponseEntity<IndividualCustomerResponseDto> getByPublicId(@PathVariable UUID publicId) {
        return ResponseEntity.ok(service.getByPublicId(publicId));
    }

    @Operation(summary = "Get all individual customers (paginated)")
    @GetMapping
    @PreAuthorize("hasAuthority('customer:read')")
    public ResponseEntity<PageResponse<IndividualCustomerResponseDto>> getAll(
            @ParameterObject CustomerFilters filters,
            @ParameterObject @PageableDefault(size = 20, sort = "lastName") Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(filters, pageable));
    }

    @Operation(summary = "Get all individual customers as a list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('customer:read')")
    public ResponseEntity<List<IndividualCustomerResponseDto>> getList(@ParameterObject CustomerFilters filters) {
        return ResponseEntity.ok(service.findAll(filters));
    }

    @Operation(summary = "Create a new individual customer")
    @PostMapping
    @PreAuthorize("hasAuthority('customer:create')")
    public ResponseEntity<IndividualCustomerResponseDto> create(@Validated @RequestBody IndividualCustomerCreateDto dto) {
        IndividualCustomerResponseDto created = service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update an existing individual customer")
    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('customer:update')")
    public ResponseEntity<IndividualCustomerResponseDto> update(@PathVariable UUID publicId, @Validated @RequestBody IndividualCustomerUpdateDto dto) {
        return ResponseEntity.ok(service.update(publicId, dto));
    }

    @Operation(summary = "Delete an individual customer")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('customer:delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID publicId) {
        service.delete(publicId);
        return ResponseEntity.noContent().build();
    }
}

