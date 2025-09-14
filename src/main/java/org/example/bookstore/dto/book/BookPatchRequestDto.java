package org.example.bookstore.dto.book;

import java.math.BigDecimal;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class BookPatchRequestDto {
    private final JsonNullable<String> title = JsonNullable.undefined();
    private final JsonNullable<BigDecimal> price = JsonNullable.undefined();
    private final JsonNullable<BigDecimal> rating = JsonNullable.undefined();
    private final JsonNullable<Integer> quantity = JsonNullable.undefined();
}
