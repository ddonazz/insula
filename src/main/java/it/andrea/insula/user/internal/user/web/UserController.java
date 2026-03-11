package it.andrea.insula.user.internal.user.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.security.PermissionAuthority;
import it.andrea.insula.user.internal.user.dto.request.*;
import it.andrea.insula.user.internal.user.dto.response.UserResponseDto;
import it.andrea.insula.user.internal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get a user by Public ID (UUID)")
    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.USER_READ + "')")
    public ResponseEntity<UserResponseDto> getByPublicId(@PathVariable UUID publicId) {
        UserResponseDto userDto = userService.getByPublicId(publicId);
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Get all users")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.USER_READ + "')")
    public ResponseEntity<PageResponse<UserResponseDto>> getAll(
            @ParameterObject UserSearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "username") Pageable pageable
    ) {
        PageResponse<UserResponseDto> response = userService.getAll(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all users as a list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.USER_READ + "')")
    public ResponseEntity<List<UserResponseDto>> getList(@ParameterObject UserSearchCriteria criteria) {
        List<UserResponseDto> response = userService.findAll(criteria);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.USER_CREATE + "')")
    public ResponseEntity<UserResponseDto> create(@Validated @RequestBody UserCreateDto dto) {
        UserResponseDto createdUser = userService.create(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(createdUser.publicId())
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }

    @Operation(summary = "Update an existing user")
    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.USER_UPDATE + "')")
    public ResponseEntity<UserResponseDto> update(@PathVariable UUID publicId, @Validated @RequestBody UserUpdateDto dto) {
        UserResponseDto updatedUser = userService.update(publicId, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Patch an existing user")
    @PatchMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.USER_UPDATE + "')")
    public ResponseEntity<UserResponseDto> patch(@PathVariable UUID publicId, @Validated @RequestBody UserPatchDto dto) {
        UserResponseDto updatedUser = userService.patch(publicId, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Activate a user")
    @PutMapping("/{publicId}/activate")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.USER_UPDATE + "')")
    public ResponseEntity<Void> activate(@PathVariable UUID publicId) {
        userService.activateUser(publicId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Suspend a user")
    @PutMapping("/{publicId}/suspend")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.USER_UPDATE + "')")
    public ResponseEntity<Void> suspend(@PathVariable UUID publicId) {
        userService.suspendUser(publicId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.USER_DELETE + "')")
    public ResponseEntity<Void> delete(@PathVariable UUID publicId) {
        userService.delete(publicId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ottieni i dati dell'utente attualmente loggato (whoami)")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> whoami(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponseDto userDto = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Aggiorna il profilo dell'utente attualmente loggato")
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Validated @RequestBody UserProfileUpdateDto dto
    ) {
        UserResponseDto userDto = userService.updateProfile(userDetails.getUsername(), dto);
        return ResponseEntity.ok(userDto);
    }
}
