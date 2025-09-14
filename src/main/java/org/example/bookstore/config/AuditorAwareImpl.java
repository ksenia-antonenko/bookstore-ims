package org.example.bookstore.config;

import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.bookstore.auth.CurrentUser;
import org.example.bookstore.domain.user.User;
import org.example.bookstore.util.Constants;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final CurrentUser currentUser;

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(currentUser.getUserDetails())
            .map(User::getUsername)
            .or(() -> Optional.of(Constants.DEFAULT_USER_NAME));
    }
}
