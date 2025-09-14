package org.example.bookstore.auth;

import static org.example.bookstore.auth.PermissionAuthority.BASIC_AUTH_AUTHORITY;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookstore.domain.user.User;
import org.example.bookstore.util.Constants;

@Slf4j
@RequiredArgsConstructor
public class CurrentUserImpl implements CurrentUser {

    private final BookstoreSecurityProvider securityProvider;

    @Override
    public Set<String> getUserAuthorities() {
        return securityProvider.getUserAuthorities();
    }

    @Override
    public boolean isBasicAuthUsed() {
        final boolean isBasicAuthUsed = securityProvider.getUserAuthorities()
            .contains(BASIC_AUTH_AUTHORITY);
        log.debug("Basic authentication is used: {}", isBasicAuthUsed);
        return isBasicAuthUsed;
    }

    @Override
    public User getUserDetails() {
        if (isBasicAuthUsed()) {
            return User.builder()
                .username(securityProvider.getBasicUsername().orElse(Constants.DEFAULT_USER_NAME))
                .build();
        }
        return securityProvider.getUserDetails().orElse(User.builder().build());
    }
}
