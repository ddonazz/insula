package it.andrea.insula.user.internal.role.mapper;

import it.andrea.insula.user.internal.role.dto.request.RolePatchDto;
import it.andrea.insula.user.internal.role.model.Role;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class RolePatchMapper implements BiFunction<RolePatchDto, Role, Role> {

    @Override
    public Role apply(RolePatchDto dto, Role role) {
        if (dto.name() != null) {
            role.setName(dto.name());
        }
        if (dto.description() != null) {
            role.setDescription(dto.description());
        }
        return role;
    }
}

