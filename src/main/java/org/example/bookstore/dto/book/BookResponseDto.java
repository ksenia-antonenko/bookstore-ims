package org.example.bookstore.dto.book;

import java.math.BigDecimal;
import java.util.List;
import org.example.bookstore.dto.author.AuthorDto;
import org.example.bookstore.dto.genre.GenreDto;

public record BookResponseDto(
    Long id,
    String title,
    List<AuthorDto> authors,
    List<GenreDto> genres,
    BigDecimal price,
    Integer quantity,
    BigDecimal rating
) {
}
