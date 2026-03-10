package it.andrea.insula.user.internal.user.service;

import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.user.internal.user.exception.UserErrorCodes;
import it.andrea.insula.user.internal.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateCreate(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new ResourceInUseException(UserErrorCodes.USERNAME_ALREADY_EXISTS, username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResourceInUseException(UserErrorCodes.EMAIL_ALREADY_EXISTS, email);
        }
    }

    public void validateUpdate(Long id, String username, String originalUsername, String email, String originalEmail) {
        if (username != null && !username.equals(originalUsername)) {
            if (userRepository.existsByUsernameAndIdNot(username, id)) {
                throw new ResourceInUseException(UserErrorCodes.USERNAME_ALREADY_EXISTS, username);
            }
        }
        if (email != null && !email.equals(originalEmail)) {
            if (userRepository.existsByEmailAndIdNot(email, id)) {
                throw new ResourceInUseException(UserErrorCodes.EMAIL_ALREADY_EXISTS, email);
            }
        }
    }

    public void validateEmailUpdate(Long id, String email, String originalEmail) {
        if (email != null && !email.equals(originalEmail)) {
            if (userRepository.existsByEmailAndIdNot(email, id)) {
                throw new ResourceInUseException(UserErrorCodes.EMAIL_ALREADY_EXISTS, email);
            }
        }
    }
}

