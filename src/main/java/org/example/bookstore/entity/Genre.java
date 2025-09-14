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
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_genre_name", columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre extends BaseEntity {

    @Column(nullable = false)
    private String name;
}
