package org.example.bookstore.mapper;

import org.example.bookstore.config.MappingConfig;
import org.example.bookstore.dto.author.AuthorDto;
import org.example.bookstore.entity.Author;
import org.mapstruct.Mapper;

@Mapper(config = MappingConfig.class)
public interface AuthorMapper {

    AuthorDto mapToDto(Author author);
}
