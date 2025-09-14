package org.example.bookstore.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Permissions as string to use in @PreAuthorize().
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermissionAuthority {

    public static final String MANAGE_ALL = "MANAGE_ALL";
    public static final String READ_ONLY = "READ_ONLY";

    public static final String BASIC_AUTH_AUTHORITY = "BASIC_AUTH_AUTHORITY";
}
