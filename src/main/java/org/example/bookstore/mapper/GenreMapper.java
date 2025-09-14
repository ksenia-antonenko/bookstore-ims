package org.example.bookstore.mapper;

import org.example.bookstore.config.MappingConfig;
import org.example.bookstore.dto.genre.GenreDto;
import org.example.bookstore.entity.Genre;
import org.mapstruct.Mapper;

@Mapper(config = MappingConfig.class, uses = {EntityIdMapper.class,
    JsonNullableMapper.class})
public interface GenreMapper {

    GenreDto mapToDto(Genre genre);
}
