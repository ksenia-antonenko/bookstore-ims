package org.example.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(indexes = {
    @Index(name = "idx_book_title", columnList = "title"),
    @Index(name = "idx_book_price", columnList = "price")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Book extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @ManyToMany
    @JoinTable(name = "book_author",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "book_genre",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new LinkedHashSet<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column
    private Integer quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal rating;

    @Builder(toBuilder = true)
    public Book(Long id, ZonedDateTime createdAt, ZonedDateTime updatedAt, String createdBy, String updatedBy,
                String title, Set<Author> authors, Set<Genre> genres, BigDecimal price,
                Integer quantity, BigDecimal rating) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.title = title;
        this.authors = authors;
        this.genres = genres;
        this.price = price;
        this.quantity = quantity;
        this.rating = rating;
    }
}