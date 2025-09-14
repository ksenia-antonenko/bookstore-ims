package org.example.bookstore.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequestDto {
    private int page = 0;
    private int size = 10;
    private SortDto sort = new SortDto();

    @Data
    public static class SortDto {
        private List<OrderDto> orders = new ArrayList<>();
    }

    @Data
    public static class OrderDto {

        private String property;
        private Sort.Direction direction = Sort.Direction.ASC;
    }
}
