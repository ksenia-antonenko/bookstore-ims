package org.example.bookstore.mapper;

import org.example.bookstore.config.MappingConfig;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(config = MappingConfig.class)
public interface JsonNullableMapper {

    default <T> T unwrap(JsonNullable<T> jsonNullable) {
        return jsonNullable == null ? null : jsonNullable.orElse(null);
    }

    /**
     * Checks whether nullable parameter was passed explicitly.
     *
     * @return true if value was set explicitly, false otherwise
     */
    @Named("isJsonNullablePresent")
    @Condition
    default <T> boolean isPresent(JsonNullable<T> nullable) {
        return nullable != null && nullable.isPresent();
    }
}