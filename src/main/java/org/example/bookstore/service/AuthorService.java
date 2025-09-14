package org.example.bookstore.service;

import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookstore.entity.Author;
import org.example.bookstore.exception.BookstoreEntityNotFoundException;
import org.example.bookstore.repository.AuthorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional
    public Author create(String firstName, String lastName) {
        String fn = normalize(firstName);
        String ln = normalize(lastName);

        Author author = Author.builder().firstName(fn).lastName(ln).build();
        return authorRepository.save(author);
    }

    @Transactional
    public Author get(Long id) {
        return authorRepository.findById(id)
            .orElseThrow(() -> new BookstoreEntityNotFoundException(id, Author.class));
    }

    @Transactional
    public List<Author> getAllByIds(Collection<Long> ids) {
        return authorRepository.findAllById(ids);
    }

    @Transactional
    public Page<Author> list(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }

    @Transactional
    public Author update(Long id, String newFirst, String newLast) {
        Author existing = get(id);

        String fn = normalize(newFirst);
        String ln = normalize(newLast);

        existing.setFirstName(fn);
        existing.setLastName(ln);
        return authorRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        get(id);
        authorRepository.deleteById(id);
    }

    private String normalize(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        String trimmed = s.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Field must not be blank");
        }
        return trimmed;
    }
}
