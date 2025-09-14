package org.example.bookstore.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class User {

    String username;
    String email;
    String lastName;
    String firstName;
    UserStatus status;
}
