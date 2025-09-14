package org.example.bookstore.auth;

import java.util.Set;
import org.example.bookstore.domain.user.User;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDeniedException;

public interface CurrentUser {

    /**
     * Returns true if current user is authenticated with basic authentication.
     */
    boolean isBasicAuthUsed();

    static RuntimeException createException(String message) {
        return new AuthorizationDeniedException(message, new AuthorizationDecision(false));
    }

    Set<String> getUserAuthorities();

    User getUserDetails();
}
