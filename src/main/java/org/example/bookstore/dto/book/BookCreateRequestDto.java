package org.example.bookstore.dto.book;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class BookCreateRequestDto {
    @NotBlank
    private String title;
    @DecimalMin(value = "0.0")
    private BigDecimal price;
    private Integer quantity;
    private Long rating;

}
