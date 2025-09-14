package org.example.bookstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookstore.auth.PermissionAuthority;
import org.example.bookstore.dto.SearchResponseDto;
import org.example.bookstore.dto.book.BookCreateRequestDto;
import org.example.bookstore.dto.book.BookPatchRequestDto;
import org.example.bookstore.dto.book.BookResponseDto;
import org.example.bookstore.dto.book.BookSearchRequestDto;
import org.example.bookstore.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {


    private final BookService bookService;

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('" + PermissionAuthority.MANAGE_ALL
        + "', '" + PermissionAuthority.READ_ONLY + "')")
    public SearchResponseDto<BookResponseDto> search(
        @RequestBody(required = false) @Valid BookSearchRequestDto request) {
        if (request == null) {
            request = new BookSearchRequestDto();
        }
        return bookService.search(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + PermissionAuthority.MANAGE_ALL
        + "', '" + PermissionAuthority.READ_ONLY + "')")
    public BookResponseDto get(@PathVariable Long id) {
        return bookService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('" + PermissionAuthority.MANAGE_ALL + "')")
    public BookResponseDto create(@RequestBody @Valid BookCreateRequestDto request) {
        return bookService.create(request);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + PermissionAuthority.MANAGE_ALL + "')")
    public BookResponseDto patch(@PathVariable Long id,
                                 @RequestBody @Valid BookPatchRequestDto request) {
        return bookService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('" + PermissionAuthority.MANAGE_ALL + "')")
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
}
