package it.andrea.insula.user.internal.permission.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.user.internal.permission.dto.response.PermissionResponseDto;
import it.andrea.insula.user.internal.permission.mapper.PermissionToPermissionResponseDtoMapper;
import it.andrea.insula.user.internal.permission.model.Permission;
import it.andrea.insula.user.internal.permission.model.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionToPermissionResponseDtoMapper responseMapper;

    public PageResponse<PermissionResponseDto> getAll(Pageable pageable) {
        Page<Permission> permissionsPage = permissionRepository.findAll(pageable);
        Page<PermissionResponseDto> dtoPage = permissionsPage.map(responseMapper);
        return PageResponse.fromPage(dtoPage);
    }
}