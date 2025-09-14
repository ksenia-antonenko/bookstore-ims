package org.example.bookstore.mapper;

import java.util.List;
import java.util.function.Function;
import org.example.bookstore.config.MappingConfig;
import org.example.bookstore.dto.SearchRequestDto;
import org.example.bookstore.dto.SearchResponseDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Mapper(config = MappingConfig.class)
public abstract class CommonMapper {

    /**
     * Map page of entity to dto response.
     *
     * @param <D> dto type
     * @param <T> entyty type
     */
    public <D, T> SearchResponseDto<D> mapPage(Page<T> page, Function<T, D> mapper) {
        return new SearchResponseDto<>(page.stream().map(mapper).toList(), page.getNumber(), page.getSize(),
            page.getTotalElements(), page.getTotalPages());
    }

    public Pageable mapPageable(SearchRequestDto requestDto) {
        return PageRequest.of(requestDto.getPage(), requestDto.getSize(), mapSort(requestDto.getSort()));
    }

    public Sort mapSort(SearchRequestDto.SortDto sortDto) {
        return Sort.by(mapOrder(sortDto.getOrders()));
    }

    public abstract List<Sort.Order> mapOrder(List<SearchRequestDto.OrderDto> orderDto);

    public Sort.Order mapOrder(SearchRequestDto.OrderDto orderDto) {
        return new Sort.Order(orderDto.getDirection(), orderDto.getProperty());
    }
}
