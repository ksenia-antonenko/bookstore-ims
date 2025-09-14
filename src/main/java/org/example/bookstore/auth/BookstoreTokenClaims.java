package org.example.bookstore.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BookstoreTokenClaims {

    ID_TOKEN_USERNAME("cognito:username"),
    FIRST_NAME("given_name"),
    LAST_NAME("family_name"),
    EMAIL("email"),
    EMAIL_VERIFIED("email_verified");

    @Getter
    private final String value;
}
