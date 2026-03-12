package it.andrea.insula.user.internal.role.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ImmutableResourceException;
import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.security.PermissionAuthority;
import it.andrea.insula.user.internal.permission.model.Permission;
import it.andrea.insula.user.internal.permission.model.PermissionRepository;
import it.andrea.insula.user.internal.role.dto.request.RoleCreateDto;
import it.andrea.insula.user.internal.role.dto.request.RolePatchDto;
import it.andrea.insula.user.internal.role.dto.request.RoleUpdateDto;
import it.andrea.insula.user.internal.role.dto.response.RoleResponseDto;
import it.andrea.insula.user.internal.role.mapper.RoleCreateDtoToRoleMapper;
import it.andrea.insula.user.internal.role.mapper.RolePatchMapper;
import it.andrea.insula.user.internal.role.mapper.RoleToRoleResponseDtoMapper;
import it.andrea.insula.user.internal.role.mapper.RoleUpdateMapper;
import it.andrea.insula.user.internal.role.model.Role;
import it.andrea.insula.user.internal.role.model.RoleRepository;
import it.andrea.insula.user.internal.user.exception.UserErrorCodes;
import it.andrea.insula.user.internal.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private RoleValidator roleValidator;
    @Mock
    private RoleToRoleResponseDtoMapper roleResponseMapper;
    @Mock
    private RoleCreateDtoToRoleMapper roleCreateMapper;
    @Mock
    private RoleUpdateMapper roleUpdateMapper;
    @Mock
    private RolePatchMapper rolePatchMapper;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private RoleResponseDto responseDto;
    private UUID publicId;

    @BeforeEach
    void setUp() {
        publicId = UUID.randomUUID();
        role = new Role();
        role.setId(1L);
        role.setPublicId(publicId);
        role.setName("MANAGER");
        role.setDescription("Manager role");
        role.setPermissions(new HashSet<>());
        role.setUsers(new HashSet<>());

        responseDto = RoleResponseDto.builder()
                .publicId(publicId)
                .name("MANAGER")
                .description("Manager role")
                .permissions(Collections.emptySet())
                .build();
    }

    @Test
    void getByPublicId_shouldReturnRole() {
        when(roleRepository.findByPublicId(publicId)).thenReturn(Optional.of(role));
        when(roleResponseMapper.apply(role)).thenReturn(responseDto);

        RoleResponseDto result = roleService.getByPublicId(publicId);

        assertThat(result.name()).isEqualTo("MANAGER");
    }

    @Test
    void getByPublicId_shouldThrowWhenNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(roleRepository.findByPublicId(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getByPublicId(unknownId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createRole_shouldCreateSuccessfully() {
        RoleCreateDto dto = new RoleCreateDto("MANAGER", "Manager role", Set.of(1L));
        Permission perm = Permission.builder().id(1L).authority(PermissionAuthority.Constants.USER_READ).description("Read").domain(PermissionAuthority.Domains.USER).build();

        doNothing().when(roleValidator).validateCreate("MANAGER");
        when(roleCreateMapper.apply(dto)).thenReturn(role);
        when(permissionRepository.findAllById(Set.of(1L))).thenReturn(List.of(perm));
        when(roleRepository.save(role)).thenReturn(role);
        when(roleResponseMapper.apply(role)).thenReturn(responseDto);

        RoleResponseDto result = roleService.createRole(dto);

        assertThat(result.name()).isEqualTo("MANAGER");
        verify(roleRepository).save(role);
    }

    @Test
    void createRole_shouldThrowWhenNameExists() {
        RoleCreateDto dto = new RoleCreateDto("MANAGER", "Manager role", Set.of(1L));
        doThrow(new ResourceInUseException(UserErrorCodes.ROLE_NAME_EXISTS, "MANAGER"))
                .when(roleValidator).validateCreate("MANAGER");

        assertThatThrownBy(() -> roleService.createRole(dto))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void createRole_shouldThrowWhenPermissionsNotFound() {
        RoleCreateDto dto = new RoleCreateDto("MANAGER", "Manager role", Set.of(1L, 99L));
        Permission perm = Permission.builder().id(1L).authority(PermissionAuthority.Constants.USER_READ).description("Read").domain(PermissionAuthority.Domains.USER).build();

        doNothing().when(roleValidator).validateCreate("MANAGER");
        when(roleCreateMapper.apply(dto)).thenReturn(role);
        when(permissionRepository.findAllById(Set.of(1L, 99L))).thenReturn(List.of(perm));

        assertThatThrownBy(() -> roleService.createRole(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // === UPDATE (PUT) ===

    @Test
    void updateRole_shouldUpdateSuccessfully() {
        RoleUpdateDto dto = RoleUpdateDto.builder().name("SUPERADMIN").description("Super admin").permissions(Set.of(1L)).build();
        Permission perm = Permission.builder().id(1L).authority(PermissionAuthority.Constants.USER_READ).description("Read").domain(PermissionAuthority.Domains.USER).build();

        when(roleRepository.findByPublicId(publicId)).thenReturn(Optional.of(role));
        doNothing().when(roleValidator).validateNotAssignedToAdmin(role);
        doNothing().when(roleValidator).validateUpdate(1L, "SUPERADMIN", "MANAGER");
        when(roleUpdateMapper.apply(dto, role)).thenReturn(role);
        when(permissionRepository.findAllById(Set.of(1L))).thenReturn(List.of(perm));
        when(roleRepository.save(role)).thenReturn(role);
        when(roleResponseMapper.apply(role)).thenReturn(responseDto);

        RoleResponseDto result = roleService.updateRole(publicId, dto);

        assertThat(result).isNotNull();
        verify(roleUpdateMapper).apply(dto, role);
    }

    @Test
    void updateRole_shouldThrowWhenRoleAssignedToAdmin() {
        User adminUser = new User();
        adminUser.setSystemAdmin(true);
        role.getUsers().add(adminUser);

        RoleUpdateDto dto = RoleUpdateDto.builder().name("RENAMED").permissions(Set.of()).build();
        when(roleRepository.findByPublicId(publicId)).thenReturn(Optional.of(role));
        doThrow(new ImmutableResourceException(UserErrorCodes.ADMIN_ROLE_IMMUTABLE))
                .when(roleValidator).validateNotAssignedToAdmin(role);

        assertThatThrownBy(() -> roleService.updateRole(publicId, dto))
                .isInstanceOf(ImmutableResourceException.class);
    }

    @Test
    void updateRole_shouldThrowWhenNameTaken() {
        RoleUpdateDto dto = RoleUpdateDto.builder().name("TAKEN").permissions(Set.of()).build();
        when(roleRepository.findByPublicId(publicId)).thenReturn(Optional.of(role));
        doNothing().when(roleValidator).validateNotAssignedToAdmin(role);
        doThrow(new ResourceInUseException(UserErrorCodes.ROLE_NAME_EXISTS, "TAKEN"))
                .when(roleValidator).validateUpdate(1L, "TAKEN", "MANAGER");

        assertThatThrownBy(() -> roleService.updateRole(publicId, dto))
                .isInstanceOf(ResourceInUseException.class);
    }

    // === PATCH ===

    @Test
    void patchRole_shouldPatchSuccessfully() {
        RolePatchDto dto = RolePatchDto.builder().name("SUPERADMIN").description("Super admin").build();
        when(roleRepository.findByPublicId(publicId)).thenReturn(Optional.of(role));
        doNothing().when(roleValidator).validateNotAssignedToAdmin(role);
        doNothing().when(roleValidator).validateUpdate(1L, "SUPERADMIN", "MANAGER");
        when(rolePatchMapper.apply(dto, role)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);
        when(roleResponseMapper.apply(role)).thenReturn(responseDto);

        RoleResponseDto result = roleService.patchRole(publicId, dto);

        assertThat(result).isNotNull();
        verify(rolePatchMapper).apply(dto, role);
    }

    @Test
    void patchRole_shouldThrowWhenRoleAssignedToAdmin() {
        User adminUser = new User();
        adminUser.setSystemAdmin(true);
        role.getUsers().add(adminUser);

        RolePatchDto dto = RolePatchDto.builder().name("RENAMED").build();
        when(roleRepository.findByPublicId(publicId)).thenReturn(Optional.of(role));
        doThrow(new ImmutableResourceException(UserErrorCodes.ADMIN_ROLE_IMMUTABLE))
                .when(roleValidator).validateNotAssignedToAdmin(role);

        assertThatThrownBy(() -> roleService.patchRole(publicId, dto))
                .isInstanceOf(ImmutableResourceException.class);
    }

    @Test
    void patchRole_shouldSkipNameCheckWhenUnchanged() {
        RolePatchDto dto = RolePatchDto.builder().name("MANAGER").description("Updated desc").build();
        when(roleRepository.findByPublicId(publicId)).thenReturn(Optional.of(role));
        doNothing().when(roleValidator).validateNotAssignedToAdmin(role);
        doNothing().when(roleValidator).validateUpdate(1L, "MANAGER", "MANAGER");
        when(rolePatchMapper.apply(dto, role)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);
        when(roleResponseMapper.apply(role)).thenReturn(responseDto);

        roleService.patchRole(publicId, dto);

        verify(rolePatchMapper).apply(dto, role);
    }

    // === DELETE ===

    @Test
    void deleteRole_shouldDeleteSuccessfully() {
        when(roleRepository.findByPublicId(publicId)).thenReturn(Optional.of(role));
        doNothing().when(roleValidator).validateNotAssignedToAdmin(role);
        doNothing().when(roleValidator).validateDelete(role);

        roleService.deleteRole(publicId);

        verify(roleRepository).delete(role);
    }

    @Test
    void deleteRole_shouldThrowWhenRoleAssignedToAdmin() {
        User adminUser = new User();
        adminUser.setSystemAdmin(true);
        role.getUsers().add(adminUser);

        when(roleRepository.findByPublicId(publicId)).thenReturn(Optional.of(role));
        doThrow(new ImmutableResourceException(UserErrorCodes.ADMIN_ROLE_IMMUTABLE))
                .when(roleValidator).validateNotAssignedToAdmin(role);

        assertThatThrownBy(() -> roleService.deleteRole(publicId))
                .isInstanceOf(ImmutableResourceException.class);
    }

    @Test
    void deleteRole_shouldThrowWhenRoleInUse() {
        User user = new User();
        role.getUsers().add(user);
        when(roleRepository.findByPublicId(publicId)).thenReturn(Optional.of(role));
        doNothing().when(roleValidator).validateNotAssignedToAdmin(role);
        doThrow(new ResourceInUseException(UserErrorCodes.ROLE_IN_USE, publicId))
                .when(roleValidator).validateDelete(role);

        assertThatThrownBy(() -> roleService.deleteRole(publicId))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void deleteRole_shouldThrowWhenNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(roleRepository.findByPublicId(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.deleteRole(unknownId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_shouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Role> page = new PageImpl<>(List.of(role), pageable, 1);
        when(roleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(roleResponseMapper.apply(role)).thenReturn(responseDto);

        PageResponse<RoleResponseDto> result = roleService.getAll(null, pageable);

        assertThat(result.content()).hasSize(1);
    }
}
