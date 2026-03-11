package it.andrea.insula.user.internal.user.mapper;

import it.andrea.insula.user.internal.user.dto.request.UserProfileUpdateDto;
import it.andrea.insula.user.internal.user.model.User;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class UserProfilePatchMapper implements BiFunction<UserProfileUpdateDto, User, User> {

    @Override
    public User apply(UserProfileUpdateDto dto, User user) {
        if (dto.email() != null) {
            user.setEmail(dto.email());
        }
        return user;
    }
}

