package org.example.bookstore.dto.book;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.bookstore.dto.SearchRequestDto;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public final class BookSearchRequestDto extends SearchRequestDto {
    // contains, case-insensitive
    private String title;
    private List<Long> authorIds;
    // any (OR), case-insensitive, matches first/last or "full name"
    private List<String> authorNames;
    private List<Long> genreIds;
    // any (OR), case-insensitive
    private List<String> genreNames;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minQuantity;
    private Integer maxQuantity;
    private BigDecimal minRating;
    private BigDecimal maxRating;

    public BookSearchRequestDto(
        int page, int size, SortDto sort,
        String title,
        List<Long> authorIds,
        List<String> authorNames,
        List<Long> genreIds,
        List<String> genreNames,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer minQuantity,
        Integer maxQuantity,
        BigDecimal minRating,
        BigDecimal maxRating
    ) {
        super(page, size, sort);
        this.title = title;
        this.authorIds = authorIds;
        this.authorNames = authorNames;
        this.genreIds = genreIds;
        this.genreNames = genreNames;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.minRating = minRating;
        this.maxRating = maxRating;
    }
}
