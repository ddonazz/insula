package it.andrea.insula.user.internal.permission.mapper;

import it.andrea.insula.user.internal.permission.dto.response.PermissionResponseDto;
import it.andrea.insula.user.internal.permission.model.Permission;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PermissionToPermissionResponseDtoMapper implements Function<Permission, PermissionResponseDto> {
    @Override
    public PermissionResponseDto apply(Permission permission) {
        return new PermissionResponseDto(
                permission.getId(),
                permission.getAuthority(),
                permission.getDescription()
        );
    }
}