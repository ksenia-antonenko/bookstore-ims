package org.example.bookstore.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookstore.dto.SearchResponseDto;
import org.example.bookstore.dto.book.BookCreateRequestDto;
import org.example.bookstore.dto.book.BookPatchRequestDto;
import org.example.bookstore.dto.book.BookResponseDto;
import org.example.bookstore.dto.book.BookSearchRequestDto;
import org.example.bookstore.entity.Author;
import org.example.bookstore.entity.Book;
import org.example.bookstore.entity.Genre;
import org.example.bookstore.exception.BookstoreEntityNotFoundException;
import org.example.bookstore.mapper.BookMapper;
import org.example.bookstore.mapper.CommonMapper;
import org.example.bookstore.repository.BookRepository;
import org.example.bookstore.specification.BookSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final CommonMapper commonMapper;
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final GenreService genreService;

    @Transactional
    public BookResponseDto create(BookCreateRequestDto requestDto) {

        log.debug("Creating a new book: {}", requestDto);
        Book book = bookMapper.map(requestDto);
        book = bookRepository.save(book);
        return bookMapper.toResponse(book);
    }

    @Transactional
    public SearchResponseDto<BookResponseDto> search(BookSearchRequestDto requestDto) {
        log.debug("Searching for books: {}", requestDto);
        Specification<Book> spec = Specification
            .where(BookSpecs.titleContains(requestDto.getTitle()))
            .and(BookSpecs.authorIdsAny(requestDto.getAuthorIds()))
            .and(BookSpecs.authorNamesAny(requestDto.getAuthorNames()))
            .and(BookSpecs.genreIdsAny(requestDto.getGenreIds()))
            .and(BookSpecs.genreNamesAny(requestDto.getGenreNames()))
            .and(BookSpecs.priceBetween(requestDto.getMinPrice(), requestDto.getMaxPrice()))
            .and(BookSpecs.quantityBetween(requestDto.getMinQuantity(), requestDto.getMaxQuantity()))
            .and(BookSpecs.ratingBetween(requestDto.getMinRating(), requestDto.getMaxRating()));

        Pageable pageable = commonMapper.mapPageable(requestDto);
        Page<Book> books = bookRepository.findAll(spec, pageable);
        return commonMapper.mapPage(books, bookMapper::toResponse);
    }

    @Transactional
    public BookResponseDto get(Long id) {
        log.debug("Retrieving a book by id: {}", id);
        Book book = getBook(id);
        return bookMapper.toResponse(book);
    }

    @Transactional
    public BookResponseDto patch(Long id, BookPatchRequestDto patchRequestDto) {
        log.debug("Updating a book by id: {}", id);
        Book book = getBook(id);
        bookMapper.map(patchRequestDto, book);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Deleting a book by id: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found");
        }
        bookRepository.deleteById(id);
    }

    @Transactional
    public void replaceAuthors(Long bookId, Set<Long> authorIds) {
        Book book = getBook(bookId);
        List<Author> authors = authorService.getAllByIds(authorIds);

        if (authors.size() != authorIds.size()) {
            Set<Long> tempAuthorIds = new LinkedHashSet<>(authorIds);
            authors.stream().map(Author::getId).toList().forEach(tempAuthorIds::remove);
            throw new BookstoreEntityNotFoundException(tempAuthorIds, Author.class);
        }

        book.setAuthors(new LinkedHashSet<>(authors));
    }

    @Transactional
    public void addAuthors(Long bookId, Set<Long> authorIds) {
        Book book = getBook(bookId);
        List<Author> toAdd = authorService.getAllByIds(authorIds);
        if (toAdd.isEmpty()) {
            return;
        }
        book.getAuthors().addAll(toAdd);
    }

    @Transactional
    public void removeAuthor(Long bookId, Long authorId) {
        Book book = getBook(bookId);
        Author author = authorService.get(authorId);
        book.getAuthors().remove(author);
    }

    @Transactional
    public void replaceGenres(Long bookId, Set<Long> genreIds) {
        Book book = getBook(bookId);
        List<Genre> genres = genreService.getAllByIds(genreIds);

        if (genres.size() != genreIds.size()) {
            Set<Long> tempGenreIds = new LinkedHashSet<>(genreIds);
            genres.stream().map(Genre::getId).toList().forEach(tempGenreIds::remove);
            throw new BookstoreEntityNotFoundException(tempGenreIds, Genre.class);
        }

        book.setGenres(new LinkedHashSet<>(genres));
    }

    @Transactional
    public void addGenres(Long bookId, Set<Long> genreIds) {
        Book book = getBook(bookId);
        List<Genre> toAdd = genreService.getAllByIds(genreIds);
        if (toAdd.isEmpty()) {
            return;
        }
        book.getGenres().addAll(toAdd);
    }

    @Transactional
    public void removeGenre(Long bookId, Long genreId) {
        Book book = getBook(bookId);
        Genre genre = genreService.get(genreId);
        book.getGenres().remove(genre);
    }

    private Book getBook(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookstoreEntityNotFoundException(id, Book.class));
    }
}
