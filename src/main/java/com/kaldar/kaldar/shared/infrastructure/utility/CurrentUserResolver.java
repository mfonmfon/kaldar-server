package com.kaldar.kaldar.shared.infrastructure.utility;

import com.kaldar.kaldar.shared.domain.exceptions.UserNotFoundException;
import com.kaldar.kaldar.shared.domain.model.UserEntity;
import com.kaldar.kaldar.shared.domain.repository.UserEntityRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resolves the currently authenticated {@link UserEntity} from the active
 * Spring Security context. This avoids duplicating the
 * SecurityContextHolder → email → DB lookup pattern across all modules.
 */
@Component
public class CurrentUserResolver {

    private final UserEntityRepository userEntityRepository;

    public CurrentUserResolver(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    /**
     * Returns the {@link UserEntity} for the currently authenticated user.
     *
     * @throws UserNotFoundException if the email from the JWT cannot be found in the database
     */
    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userEntityRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found: " + email));
    }

    /**
     * Returns the database ID of the currently authenticated user.
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
