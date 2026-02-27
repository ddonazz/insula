package it.andrea.insula.user.internal.user.dto.request;

import it.andrea.insula.user.internal.user.model.UserStatus;

public record UserSearchCriteria(
        String username,
        String email,
        UserStatus status
) {
}
