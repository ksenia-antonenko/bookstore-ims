package org.example.bookstore.repository;

import org.example.bookstore.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {

}