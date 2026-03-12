package it.andrea.insula.user.internal.role.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.security.PermissionAuthority;
import it.andrea.insula.user.internal.role.dto.request.RoleCreateDto;
import it.andrea.insula.user.internal.role.dto.request.RolePatchDto;
import it.andrea.insula.user.internal.role.dto.request.RoleSearchCriteria;
import it.andrea.insula.user.internal.role.dto.request.RoleUpdateDto;
import it.andrea.insula.user.internal.role.dto.response.RoleResponseDto;
import it.andrea.insula.user.internal.role.service.RoleService;
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
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "APIs for managing roles")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Get a role by public ID")
    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.ROLE_READ + "')")
    public ResponseEntity<RoleResponseDto> getByPublicId(@PathVariable UUID publicId) {
        RoleResponseDto roleDto = roleService.getByPublicId(publicId);
        return ResponseEntity.ok(roleDto);
    }

    @Operation(summary = "Get all roles")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.ROLE_READ + "')")
    public ResponseEntity<PageResponse<RoleResponseDto>> getAll(
            @ParameterObject RoleSearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        PageResponse<RoleResponseDto> response = roleService.getAll(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all roles as a list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.ROLE_READ + "')")
    public ResponseEntity<List<RoleResponseDto>> getList(@ParameterObject RoleSearchCriteria criteria) {
        List<RoleResponseDto> response = roleService.findAll(criteria);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new role")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.ROLE_CREATE + "')")
    public ResponseEntity<RoleResponseDto> create(@Validated @RequestBody RoleCreateDto dto) {
        RoleResponseDto createdRole = roleService.createRole(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(createdRole.publicId())
                .toUri();

        return ResponseEntity.created(location).body(createdRole);
    }

    @Operation(summary = "Update an existing role")
    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.ROLE_UPDATE + "')")
    public ResponseEntity<RoleResponseDto> update(@PathVariable UUID publicId, @Validated @RequestBody RoleUpdateDto dto) {
        RoleResponseDto updatedRole = roleService.updateRole(publicId, dto);
        return ResponseEntity.ok(updatedRole);
    }

    @Operation(summary = "Patch an existing role")
    @PatchMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.ROLE_UPDATE + "')")
    public ResponseEntity<RoleResponseDto> patch(@PathVariable UUID publicId, @Validated @RequestBody RolePatchDto dto) {
        RoleResponseDto patchedRole = roleService.patchRole(publicId, dto);
        return ResponseEntity.ok(patchedRole);
    }

    @Operation(summary = "Delete a role")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.ROLE_DELETE + "')")
    public ResponseEntity<Void> delete(@PathVariable UUID publicId) {
        roleService.deleteRole(publicId);
        return ResponseEntity.noContent().build();
    }
}
