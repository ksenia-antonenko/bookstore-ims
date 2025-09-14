package org.example.bookstore.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.example.bookstore.dto.SearchRequestDto;
import org.example.bookstore.dto.SearchResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class CommonMapperTest {

    static CommonMapper mapper;

    @BeforeAll
    static void init() {
        mapper = Mappers.getMapper(CommonMapper.class);
        assertThat(mapper).isNotNull();
    }

    // -------- mapPage --------

    @Test
    @DisplayName("mapPage maps Page<T> to SearchResponseDto<D> using provided mapper")
    void mapPage_mapsContentAndMeta() {
        // Given a page of strings
        Pageable pageable = PageRequest.of(2, 3, Sort.by(Sort.Direction.DESC, "any"));
        Page<String> page = new PageImpl<>(List.of("aa", "bbb"), pageable, 11);

        // When mapping to lengths
        SearchResponseDto<Integer> dto = mapper.mapPage(page, String::length);

        // Then
        assertThat(dto.page()).isEqualTo(2);
        assertThat(dto.size()).isEqualTo(3);
        assertThat(dto.totalElements()).isEqualTo(11L);
        assertThat(dto.totalPages()).isEqualTo(4); // 11 items, size 3 -> 4 pages
        assertThat(dto.content()).containsExactly(2, 3);
    }

    // -------- mapPageable --------

    @Test
    @DisplayName("mapPageable builds PageRequest with mapped Sort from SearchRequestDto")
    void mapPageable_buildsPageRequest() {
        SearchRequestDto req = new SearchRequestDto();
        req.setPage(1);
        req.setSize(5);

        SearchRequestDto.OrderDto o1 = new SearchRequestDto.OrderDto();
        o1.setProperty("title");
        o1.setDirection(Sort.Direction.ASC);

        SearchRequestDto.OrderDto o2 = new SearchRequestDto.OrderDto();
        o2.setProperty("price");
        o2.setDirection(Sort.Direction.DESC);

        SearchRequestDto.SortDto sort = new SearchRequestDto.SortDto();
        sort.getOrders().add(o1);
        sort.getOrders().add(o2);
        req.setSort(sort);

        Pageable p = mapper.mapPageable(req);

        assertThat(p.getPageNumber()).isEqualTo(1);
        assertThat(p.getPageSize()).isEqualTo(5);

        Sort s = p.getSort();
        assertThat(s.getOrderFor("title")).isNotNull();
        assertThat(s.getOrderFor("title").getDirection()).isEqualTo(Sort.Direction.ASC);

        assertThat(s.getOrderFor("price")).isNotNull();
        assertThat(s.getOrderFor("price").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    // -------- mapSort --------

    @Test
    @DisplayName("mapSort maps SortDto orders into a Spring Sort with the same order sequence")
    void mapSort_mapsOrders() {
        SearchRequestDto.OrderDto o1 = new SearchRequestDto.OrderDto();
        o1.setProperty("id");
        o1.setDirection(Sort.Direction.DESC);

        SearchRequestDto.OrderDto o2 = new SearchRequestDto.OrderDto();
        o2.setProperty("title");
        o2.setDirection(Sort.Direction.ASC);

        SearchRequestDto.SortDto sortDto = new SearchRequestDto.SortDto();
        sortDto.getOrders().add(o1);
        sortDto.getOrders().add(o2);

        Sort sort = mapper.mapSort(sortDto);

        // verify sequence is preserved
        List<Sort.Order> orders = new ArrayList<>();
        sort.forEach(orders::add);

        assertThat(orders).hasSize(2);
        assertThat(orders.get(0).getProperty()).isEqualTo("id");
        assertThat(orders.get(0).getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(orders.get(1).getProperty()).isEqualTo("title");
        assertThat(orders.get(1).getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    // -------- mapOrder (list) via MapStruct-generated method --------

    @Test
    @DisplayName("mapOrder(List<OrderDto>) maps each entry using mapOrder(OrderDto)")
    void mapOrder_list_mapsEach() {
        SearchRequestDto.OrderDto o1 = new SearchRequestDto.OrderDto();
        o1.setProperty("createdAt");
        o1.setDirection(Sort.Direction.DESC);

        SearchRequestDto.OrderDto o2 = new SearchRequestDto.OrderDto();
        o2.setProperty("title");
        o2.setDirection(Sort.Direction.ASC);

        List<SearchRequestDto.OrderDto> in = List.of(o1, o2);

        List<Sort.Order> out = mapper.mapOrder(in);

        assertThat(out).hasSize(2);
        assertThat(out.get(0).getProperty()).isEqualTo("createdAt");
        assertThat(out.get(0).getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(out.get(1).getProperty()).isEqualTo("title");
        assertThat(out.get(1).getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    // -------- mapOrder (single) explicit helper --------

    @Test
    @DisplayName("mapOrder(OrderDto) maps direction and property")
    void mapOrder_single_mapsFields() {
        SearchRequestDto.OrderDto o = new SearchRequestDto.OrderDto();
        o.setProperty("rating");
        o.setDirection(Sort.Direction.DESC);

        Sort.Order order = mapper.mapOrder(o);

        assertThat(order.getProperty()).isEqualTo("rating");
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
    }
}