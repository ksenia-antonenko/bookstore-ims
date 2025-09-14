package org.example.bookstore.controller;

import jakarta.validation.Valid;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.example.bookstore.auth.PermissionAuthority;
import org.example.bookstore.dto.IdListRequestDto;
import org.example.bookstore.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books/{bookId}/authors")
@RequiredArgsConstructor
class BookAuthorController {

    private final BookService bookService;

    @PutMapping
    @PreAuthorize("hasAnyAuthority('" + PermissionAuthority.MANAGE_ALL + "')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void replaceAuthors(@PathVariable Long bookId, @RequestBody @Valid IdListRequestDto body) {
        bookService.replaceAuthors(bookId, new HashSet<>(body.ids()));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('" + PermissionAuthority.MANAGE_ALL + "')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addAuthors(@PathVariable Long bookId, @RequestBody @Valid IdListRequestDto body) {
        bookService.addAuthors(bookId, new HashSet<>(body.ids()));
    }

    @DeleteMapping("/{authorId}")
    @PreAuthorize("hasAnyAuthority('" + PermissionAuthority.MANAGE_ALL + "')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAuthor(@PathVariable Long bookId, @PathVariable Long authorId) {
        bookService.removeAuthor(bookId, authorId);
    }
}
