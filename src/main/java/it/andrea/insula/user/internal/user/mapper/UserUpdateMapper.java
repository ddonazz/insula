package it.andrea.insula.user.internal.user.mapper;

import it.andrea.insula.user.internal.user.dto.request.UserUpdateDto;
import it.andrea.insula.user.internal.user.model.User;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class UserUpdateMapper implements BiFunction<UserUpdateDto, User, User> {

    @Override
    public User apply(UserUpdateDto dto, User user) {
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setStatus(dto.status());
        return user;
    }
}

