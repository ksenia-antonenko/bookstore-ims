package org.example.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.example.bookstore.dto.SearchResponseDto;
import org.example.bookstore.dto.book.BookCreateRequestDto;
import org.example.bookstore.dto.book.BookPatchRequestDto;
import org.example.bookstore.dto.book.BookResponseDto;
import org.example.bookstore.dto.book.BookSearchRequestDto;
import org.example.bookstore.entity.Author;
import org.example.bookstore.entity.BaseEntity;
import org.example.bookstore.entity.Book;
import org.example.bookstore.entity.Genre;
import org.example.bookstore.exception.BookstoreEntityNotFoundException;
import org.example.bookstore.mapper.BookMapper;
import org.example.bookstore.mapper.CommonMapper;
import org.example.bookstore.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookMapper bookMapper;
    @Mock
    private CommonMapper commonMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorService authorService;
    @Mock
    private GenreService genreService;

    @InjectMocks
    private BookService service;

    // ---------- create ----------

    @Test
    @DisplayName("create: maps request -> entity, saves, maps to response")
    void create_ok() {
        BookCreateRequestDto req = new BookCreateRequestDto();
        req.setTitle("Dune");
        req.setPrice(new BigDecimal("14.95"));
        req.setQuantity(3);
        req.setRating(5L);

        Book mapped = new Book();
        mapped.setId(null);
        mapped.setTitle("Dune");

        Book saved = new Book();
        saved.setId(42L);
        saved.setTitle("Dune");

        BookResponseDto resp = new BookResponseDto(42L, "Dune", List.of(), List.of(), new BigDecimal("14.95"), 3, null);

        when(bookMapper.map(req)).thenReturn(mapped);
        when(bookRepository.save(mapped)).thenReturn(saved);
        when(bookMapper.toResponse(saved)).thenReturn(resp);

        BookResponseDto out = service.create(req);
        assertThat(out).isSameAs(resp);

        verify(bookMapper).map(req);
        verify(bookRepository).save(mapped);
        verify(bookMapper).toResponse(saved);
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    // ---------- search ----------

    @Test
    @DisplayName("search: builds spec, maps pageable via CommonMapper, returns mapped page")
    void search_ok() {
        BookSearchRequestDto req = new BookSearchRequestDto();
        req.setTitle("clean");
        req.setMinPrice(new BigDecimal("0"));
        req.setMaxPrice(new BigDecimal("100"));
        req.getSort().getOrders().add(new org.example.bookstore.dto.SearchRequestDto.OrderDto());

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        when(commonMapper.mapPageable(req)).thenReturn(pageable);

        Book b = new Book();
        b.setId(1L);
        Page<Book> page = new PageImpl<>(List.of(b), pageable, 1);

        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        BookResponseDto resp = new BookResponseDto(1L, "Clean Architecture", List.of(), List.of(), null, null, null);
        when(commonMapper.mapPage(eq(page), any())).thenReturn(
            new SearchResponseDto<>(List.of(resp), 0, 10, 1L, 1)
        );

        SearchResponseDto<BookResponseDto> out = service.search(req);
        assertThat(out.totalElements()).isEqualTo(1L);
        assertThat(out.content()).singleElement().isSameAs(resp);

        verify(commonMapper).mapPageable(req);
        verify(bookRepository).findAll(any(Specification.class), eq(pageable));
        verify(commonMapper).mapPage(eq(page), any());
    }

    // ---------- get ----------

    @Test
    @DisplayName("get: returns mapped response when found")
    void get_found() {
        Book b = new Book();
        b.setId(100L);
        when(bookRepository.findById(100L)).thenReturn(Optional.of(b));

        BookResponseDto resp = new BookResponseDto(100L, "X", List.of(), List.of(), null, null, null);
        when(bookMapper.toResponse(b)).thenReturn(resp);

        BookResponseDto out = service.get(100L);
        assertThat(out).isSameAs(resp);
    }

    @Test
    @DisplayName("get: throws BookstoreEntityNotFoundException when not found")
    void get_notFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(999L))
            .isInstanceOf(BookstoreEntityNotFoundException.class);
    }

    // ---------- patch ----------

    @Test
    @DisplayName("patch: loads book, maps patch into entity, saves and returns response")
    void patch_ok() {

        Book existing = new Book();
        existing.setId(5L);
        when(bookRepository.findById(5L)).thenReturn(Optional.of(existing));

        Book saved = new Book();
        saved.setId(5L);
        when(bookRepository.save(existing)).thenReturn(saved);

        BookResponseDto resp = new BookResponseDto(5L, "Updated", List.of(), List.of(), null, null, null);
        when(bookMapper.toResponse(saved)).thenReturn(resp);

        BookPatchRequestDto patch = new BookPatchRequestDto();

        BookResponseDto out = service.patch(5L, patch);
        assertThat(out).isSameAs(resp);

        verify(bookMapper).map(patch, existing);
        verify(bookRepository).save(existing);
    }

    // ---------- delete ----------

    @Test
    @DisplayName("delete: deletes when exists")
    void delete_ok() {
        when(bookRepository.existsById(7L)).thenReturn(true);
        service.delete(7L);
        verify(bookRepository).deleteById(7L);
    }

    @Test
    @DisplayName("delete: throws EntityNotFoundException when not exists")
    void delete_notFound() {
        when(bookRepository.existsById(7L)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(7L))
            .isInstanceOf(EntityNotFoundException.class);
        verify(bookRepository, never()).deleteById(anyLong());
    }

    // ---------- authors: replace/add/remove ----------

    @Test
    @DisplayName("replaceAuthors: replaces set when all ids exist")
    void replaceAuthors_ok() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthors(new LinkedHashSet<>());

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Author a1 = author(10L);
        Author a2 = author(11L);
        when(authorService.getAllByIds(Set.of(10L, 11L))).thenReturn(List.of(a1, a2));

        service.replaceAuthors(1L, Set.of(10L, 11L));

        assertThat(book.getAuthors()).containsExactlyInAnyOrder(a1, a2);
    }

    @Test
    @DisplayName("replaceAuthors: throws when some ids are missing")
    void replaceAuthors_missingIds() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthors(new LinkedHashSet<>());
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Only 10 exists, 11 is missing
        when(authorService.getAllByIds(Set.of(10L, 11L))).thenReturn(List.of(author(10L)));

        assertThatThrownBy(() -> service.replaceAuthors(1L, Set.of(10L, 11L)))
            .isInstanceOf(BookstoreEntityNotFoundException.class);
        assertThat(book.getAuthors()).isEmpty();
    }

    @Test
    @DisplayName("addAuthors: adds to existing; ignores empty list")
    void addAuthors_ok_and_empty() {
        Book book = new Book();
        book.setId(1L);
        LinkedHashSet<Author> existing = new LinkedHashSet<>(List.of(author(10L)));
        book.setAuthors(existing);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // empty - no op
        when(authorService.getAllByIds(Set.of(99L))).thenReturn(List.of());
        service.addAuthors(1L, Set.of(99L));
        assertThat(book.getAuthors()).extracting(BaseEntity::getId).containsExactly(10L);

        // real add
        when(authorService.getAllByIds(Set.of(11L))).thenReturn(List.of(author(11L)));
        service.addAuthors(1L, Set.of(11L));
        assertThat(book.getAuthors()).extracting(Author::getId).containsExactlyInAnyOrder(10L, 11L);
    }

    @Test
    @DisplayName("removeAuthor: removes the link if present")
    void removeAuthor_ok() {
        Author a10 = author(10L);
        Author a11 = author(11L);

        Book book = new Book();
        book.setId(1L);
        book.setAuthors(new LinkedHashSet<>(List.of(a10, a11)));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(authorService.get(10L)).thenReturn(a10);

        service.removeAuthor(1L, 10L);
        assertThat(book.getAuthors()).containsExactly(a11);
    }

    // ---------- genres: replace/add/remove ----------

    @Test
    @DisplayName("replaceGenres: replaces set when all ids exist")
    void replaceGenres_ok() {
        Book book = new Book();
        book.setId(1L);
        book.setGenres(new LinkedHashSet<>());
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Genre g20 = genre(20L);
        Genre g22 = genre(22L);
        when(genreService.getAllByIds(Set.of(20L, 22L))).thenReturn(List.of(g20, g22));

        service.replaceGenres(1L, Set.of(20L, 22L));
        assertThat(book.getGenres()).containsExactlyInAnyOrder(g20, g22);
    }

    @Test
    @DisplayName("replaceGenres: throws when some ids are missing")
    void replaceGenres_missingIds() {
        Book book = new Book();
        book.setId(1L);
        book.setGenres(new LinkedHashSet<>());
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        when(genreService.getAllByIds(Set.of(20L, 22L))).thenReturn(List.of(genre(20L)));

        assertThatThrownBy(() -> service.replaceGenres(1L, Set.of(20L, 22L)))
            .isInstanceOf(BookstoreEntityNotFoundException.class);
        assertThat(book.getGenres()).isEmpty();
    }

    @Test
    @DisplayName("addGenres: adds to existing; ignores empty list")
    void addGenres_ok_and_empty() {
        Book book = new Book();
        book.setId(1L);
        LinkedHashSet<Genre> existing = new LinkedHashSet<>(List.of(genre(20L)));
        book.setGenres(existing);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        when(genreService.getAllByIds(Set.of(99L))).thenReturn(List.of());
        service.addGenres(1L, Set.of(99L));
        assertThat(book.getGenres()).extracting(BaseEntity::getId).containsExactly(20L);

        when(genreService.getAllByIds(Set.of(21L))).thenReturn(List.of(genre(21L)));
        service.addGenres(1L, Set.of(21L));
        assertThat(book.getGenres()).extracting(Genre::getId).containsExactlyInAnyOrder(20L, 21L);
    }

    @Test
    @DisplayName("removeGenre: removes the link if present")
    void removeGenre_ok() {
        Genre g20 = genre(20L);
        Genre g21 = genre(21L);

        Book book = new Book();
        book.setId(1L);
        book.setGenres(new LinkedHashSet<>(List.of(g20, g21)));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(genreService.get(20L)).thenReturn(g20);

        service.removeGenre(1L, 20L);
        assertThat(book.getGenres()).containsExactly(g21);
    }

    // ---------- helpers ----------

    private static Author author(Long id) {
        Author a = new Author();
        a.setId(id);
        return a;
    }

    private static Genre genre(Long id) {
        Genre g = new Genre();
        g.setId(id);
        return g;
    }
}
