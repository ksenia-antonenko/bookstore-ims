package org.example.bookstore.mapper;

import org.example.bookstore.config.MappingConfig;
import org.example.bookstore.dto.book.BookCreateRequestDto;
import org.example.bookstore.dto.book.BookPatchRequestDto;
import org.example.bookstore.dto.book.BookResponseDto;
import org.example.bookstore.entity.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MappingConfig.class, uses = {EntityIdMapper.class,
    JsonNullableMapper.class, AuthorMapper.class, GenreMapper.class})
public interface BookMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "title", source = "title", conditionQualifiedByName = "isJsonNullablePresent")
    @Mapping(target = "price", source = "price", conditionQualifiedByName = "isJsonNullablePresent")
    @Mapping(target = "rating", source = "rating", conditionQualifiedByName = "isJsonNullablePresent")
    void map(BookPatchRequestDto patchRequestDto, @MappingTarget Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Book map(BookCreateRequestDto createRequestDto);

    BookResponseDto toResponse(Book book);
}
