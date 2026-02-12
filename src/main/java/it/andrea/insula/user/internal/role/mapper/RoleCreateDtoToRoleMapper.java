package it.andrea.insula.user.internal.role.mapper;

import it.andrea.insula.user.internal.role.dto.request.RoleCreateDto;
import it.andrea.insula.user.internal.role.model.Role;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class RoleCreateDtoToRoleMapper implements Function<RoleCreateDto, Role> {
    @Override
    public Role apply(RoleCreateDto dto) {
        Role role = new Role();
        role.setName(dto.name());
        role.setDescription(dto.description());
        return role;
    }
}
