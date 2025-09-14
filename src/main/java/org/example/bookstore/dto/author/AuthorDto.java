package org.example.bookstore.dto.author;

import jakarta.validation.Valid;
import lombok.Data;

@Valid
@Data
public class AuthorDto {
    private Long id;
    private String firstName;
    private String lastName;
}
