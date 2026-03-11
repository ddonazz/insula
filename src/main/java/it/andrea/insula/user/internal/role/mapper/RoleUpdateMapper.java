package it.andrea.insula.user.internal.role.mapper;

import it.andrea.insula.user.internal.role.dto.request.RoleUpdateDto;
import it.andrea.insula.user.internal.role.model.Role;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class RoleUpdateMapper implements BiFunction<RoleUpdateDto, Role, Role> {

    @Override
    public Role apply(RoleUpdateDto dto, Role role) {
        role.setName(dto.name());
        role.setDescription(dto.description());
        return role;
    }
}

