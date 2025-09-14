package org.example.bookstore.dto.author;

import jakarta.validation.Valid;

@Valid
public class AuthorCreateRequestDto {
    private String firstName;
    private String lastName;
}
