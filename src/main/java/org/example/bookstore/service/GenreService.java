package org.example.bookstore.service;

import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookstore.entity.Genre;
import org.example.bookstore.exception.BookstoreEntityNotFoundException;
import org.example.bookstore.repository.GenreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    @Transactional
    public Genre create(String name) {
        String n = normalize(name);
        Genre genre = Genre.builder().name(n).build();
        return genreRepository.save(genre);
    }

    @Transactional
    public Genre get(Long id) {
        return genreRepository.findById(id)
            .orElseThrow(() -> new BookstoreEntityNotFoundException(id, Genre.class));
    }

    @Transactional
    public List<Genre> getAllByIds(Collection<Long> ids) {
        return genreRepository.findAllById(ids);
    }

    @Transactional
    public Page<Genre> list(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

    @Transactional
    public Genre update(Long id, String newName) {
        Genre existing = get(id);
        String n = normalize(newName);
        existing.setName(n);
        return genreRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        get(id); // throws if not exists
        genreRepository.deleteById(id);
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