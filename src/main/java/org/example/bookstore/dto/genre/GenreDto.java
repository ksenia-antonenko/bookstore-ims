package org.example.bookstore.dto.genre;

import jakarta.validation.Valid;
import lombok.Data;

@Valid
@Data
public class GenreDto {
    private Long id;
    private String name;
}
