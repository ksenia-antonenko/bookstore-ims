package org.example.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(
    name = "author",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_author_first_last",
        columnNames = {"first_name", "last_name"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author extends BaseEntity {

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
}
