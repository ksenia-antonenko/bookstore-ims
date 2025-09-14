package org.example.bookstore.auth;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.bookstore.domain.user.User;
import org.example.bookstore.domain.user.UserStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookstoreSecurityProvider {

    /**
     * Returns current user details.
     *
     * @return user details
     */
    public Optional<User> getUserDetails() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(authentication -> JwtAuthenticationToken.class.isAssignableFrom(authentication.getClass()))
            .map(Authentication::getPrincipal)
            .filter(principal -> Jwt.class.isAssignableFrom(principal.getClass()))
            .map(Jwt.class::cast)
            .map(principal -> User.builder()
                .username(principal.getClaim(BookstoreTokenClaims.ID_TOKEN_USERNAME.getValue()))
                .email(principal.getClaim(BookstoreTokenClaims.EMAIL.getValue()))
                .firstName(principal.getClaim(BookstoreTokenClaims.FIRST_NAME.getValue()))
                .lastName(principal.getClaim(BookstoreTokenClaims.LAST_NAME.getValue()))
                .status(UserStatus.ACTIVE) // If user got token, user is in active status
                .build());
    }

    /**
     * Returns current user's username if basic auth is used.
     */
    public Optional<String> getBasicUsername() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(
                authentication -> UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication.getClass()))
            .map(UsernamePasswordAuthenticationToken.class::cast)
            .map(UsernamePasswordAuthenticationToken::getPrincipal)
            .filter(principal -> org.springframework.security.core.userdetails.User.class
                .isAssignableFrom(principal.getClass()))
            .map(org.springframework.security.core.userdetails.User.class::cast)
            .map(org.springframework.security.core.userdetails.User::getUsername);
    }

    /**
     * Returns user's authorities.
     *
     * @return authorities set
     */
    public Set<String> getUserAuthorities() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getAuthorities)
            .stream()
            .flatMap(Collection::stream)
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
