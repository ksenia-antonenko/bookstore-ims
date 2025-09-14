package org.example.bookstore.domain.user;

/**
 * User Status.
 */
public enum UserStatus {

    ACTIVE,
    NOT_ACTIVE;

    /**
     * Get enum from string.
     *
     * @param status string status
     * @return enum element
     */
    public static UserStatus fromString(String status) {
        return UserStatus.valueOf(status.toUpperCase());
    }
}

