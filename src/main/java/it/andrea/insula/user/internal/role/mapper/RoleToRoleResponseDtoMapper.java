package it.andrea.insula.user.internal.role.mapper;

import it.andrea.insula.user.internal.permission.mapper.PermissionToPermissionResponseDtoMapper;
import it.andrea.insula.user.internal.role.dto.response.RoleResponseDto;
import it.andrea.insula.user.internal.role.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleToRoleResponseDtoMapper implements Function<Role, RoleResponseDto> {

    private final PermissionToPermissionResponseDtoMapper permissionMapper;

    @Override
    public RoleResponseDto apply(Role role) {
        return new RoleResponseDto(
                role.getName(),
                role.getDescription(),
                role.getPermissions().stream().map(permissionMapper).collect(Collectors.toSet())
        );
    }
}
