package it.andrea.insula.user.internal.role.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.user.internal.permission.model.Permission;
import it.andrea.insula.user.internal.permission.model.PermissionRepository;
import it.andrea.insula.user.internal.role.dto.request.RoleCreateDto;
import it.andrea.insula.user.internal.role.dto.request.RoleUpdateDto;
import it.andrea.insula.user.internal.role.dto.response.RoleResponseDto;
import it.andrea.insula.user.internal.role.mapper.RoleCreateDtoToRoleMapper;
import it.andrea.insula.user.internal.role.mapper.RoleToRoleResponseDtoMapper;
import it.andrea.insula.user.internal.role.model.Role;
import it.andrea.insula.user.internal.role.model.RoleRepository;
import it.andrea.insula.user.internal.user.exception.UserErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    private final RoleToRoleResponseDtoMapper roleToRoleResponseDtoMapper;
    private final RoleCreateDtoToRoleMapper roleCreateDtoToRoleMapper;

    @Transactional(readOnly = true)
    public RoleResponseDto getRole(Long id) {
        Role role = retrieveRole(id);
        return roleToRoleResponseDtoMapper.apply(role);
    }

    @Transactional(readOnly = true)
    public PageResponse<RoleResponseDto> getRoles(Pageable pageable) {
        Page<RoleResponseDto> page = roleRepository.findAll(pageable)
                .map(roleToRoleResponseDtoMapper);
        return PageResponse.fromPage(page);
    }

    @Transactional
    public RoleResponseDto createRole(RoleCreateDto request) {
        if (roleRepository.existsByName(request.name())) {
            throw new ResourceInUseException(UserErrorCodes.ROLE_NAME_EXISTS, request.name());
        }

        Role role = roleCreateDtoToRoleMapper.apply(request);

        Set<Permission> foundPermissions = computePermissions(request.permissions());
        role.setPermissions(foundPermissions);

        Role savedRole = roleRepository.save(role);
        return roleToRoleResponseDtoMapper.apply(savedRole);
    }

    @Transactional
    public RoleResponseDto updateRole(Long id, RoleUpdateDto request) {
        Role role = retrieveRole(id);

        if (request.name() != null && !request.name().equals(role.getName())) {
            if (roleRepository.existsByNameAndIdNot(request.name(), id)) {
                throw new ResourceInUseException(UserErrorCodes.ROLE_NAME_EXISTS, request.name());
            }
            role.setName(request.name());
        }

        if (request.description() != null) {
            role.setDescription(request.description());
        }

        if (request.permissions() != null) {
            Set<Permission> foundPermissions = computePermissions(request.permissions());
            role.setPermissions(foundPermissions);
        }

        Role savedRole = roleRepository.save(role);
        return roleToRoleResponseDtoMapper.apply(savedRole);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = retrieveRole(id);

        if (!role.getUsers().isEmpty()) {
            throw new ResourceInUseException(UserErrorCodes.ROLE_IN_USE, id);
        }

        roleRepository.delete(role);
    }

    private Role retrieveRole(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(UserErrorCodes.ROLE_NOT_FOUND, id));
    }

    private Set<Permission> computePermissions(Collection<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new HashSet<>();
        }

        return new HashSet<>(permissionRepository.findAllById(permissionIds));
    }
}