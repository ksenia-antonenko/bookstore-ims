package org.example.bookstore.mapper;

import org.example.bookstore.entity.BaseEntity;

/**
 * Simple dummy entity for unit tests.
 * Not annotated with @Entity because we don’t need persistence here.
 */
public class DummyEntity extends BaseEntity {

    public DummyEntity() {
    }

    public DummyEntity(Long id) {
        this.setId(id);
    }
}
