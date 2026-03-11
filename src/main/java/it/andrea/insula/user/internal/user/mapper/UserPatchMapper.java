package it.andrea.insula.user.internal.user.mapper;

import it.andrea.insula.user.internal.user.dto.request.UserPatchDto;
import it.andrea.insula.user.internal.user.model.User;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class UserPatchMapper implements BiFunction<UserPatchDto, User, User> {

    @Override
    public User apply(UserPatchDto dto, User user) {
        if (dto.username() != null) {
            user.setUsername(dto.username());
        }
        if (dto.email() != null) {
            user.setEmail(dto.email());
        }
        if (dto.status() != null) {
            user.setStatus(dto.status());
        }
        return user;
    }
}

