package org.example.bookstore.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.example.bookstore.exception.BookstoreEntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EntityIdMapperTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private EntityIdMapper mapper;

    // ---------- resolve(id, type) ----------

    @Test
    @DisplayName("resolve(id, type): returns entity when found")
    void resolve_single_found() {
        DummyEntity e = new DummyEntity(1L);
        when(em.find(DummyEntity.class, 1L)).thenReturn(e);

        DummyEntity out = mapper.resolve(1L, DummyEntity.class);
        assertThat(out).isSameAs(e);
        verify(em).find(DummyEntity.class, 1L);
    }

    @Test
    @DisplayName("resolve(id, type): returns null when id is null")
    void resolve_single_nullId_returnsNull() {
        DummyEntity out = mapper.resolve(null, DummyEntity.class);
        assertThat(out).isNull();
        verifyNoInteractions(em);
    }

    @Test
    @DisplayName("resolve(id, type): throws BookstoreEntityNotFoundException when not found")
    void resolve_single_notFound_throws() {
        when(em.find(DummyEntity.class, 99L)).thenReturn(null);

        assertThatThrownBy(() -> mapper.resolve(99L, DummyEntity.class))
            .isInstanceOf(BookstoreEntityNotFoundException.class)
            .hasMessageContaining("99");
        verify(em).find(DummyEntity.class, 99L);
    }

    // ---------- resolveForIds(ids, type) ----------

    @Test
    @DisplayName("resolveForIds(ids, type): returns LinkedHashSet with same order as input")
    void resolveForIds_found_preservesOrder() {
        LinkedHashSet<Long> ids = new LinkedHashSet<>(List.of(5L, 3L, 7L));
        DummyEntity e5 = new DummyEntity(5L);
        DummyEntity e3 = new DummyEntity(3L);
        DummyEntity e7 = new DummyEntity(7L);

        when(em.find(DummyEntity.class, 5L)).thenReturn(e5);
        when(em.find(DummyEntity.class, 3L)).thenReturn(e3);
        when(em.find(DummyEntity.class, 7L)).thenReturn(e7);

        Set<DummyEntity> out = mapper.resolveForIds(ids, DummyEntity.class);

        assertThat(out).isInstanceOf(LinkedHashSet.class);
        assertThat(out).containsExactly(e5, e3, e7); // preserves insertion order
        verify(em).find(DummyEntity.class, 5L);
        verify(em).find(DummyEntity.class, 3L);
        verify(em).find(DummyEntity.class, 7L);
    }

    @Test
    @DisplayName("resolveForIds(ids, type): returns empty set for null or empty input")
    void resolveForIds_nullOrEmpty_returnsEmpty() {
        assertThat(mapper.resolveForIds(null, DummyEntity.class)).isEmpty();
        assertThat(mapper.resolveForIds(new LinkedHashSet<>(), DummyEntity.class)).isEmpty();
        verifyNoInteractions(em);
    }

    @Test
    @DisplayName("resolveForIds(ids, type): throws when any id is missing")
    void resolveForIds_missing_throws() {
        LinkedHashSet<Long> ids = new LinkedHashSet<>(List.of(1L, 2L, 3L));
        when(em.find(DummyEntity.class, 1L)).thenReturn(new DummyEntity(1L));
        when(em.find(DummyEntity.class, 2L)).thenReturn(null); // missing triggers exception

        assertThatThrownBy(() -> mapper.resolveForIds(ids, DummyEntity.class))
            .isInstanceOf(BookstoreEntityNotFoundException.class)
            .hasMessageContaining("2");

        verify(em).find(DummyEntity.class, 1L);
        verify(em).find(DummyEntity.class, 2L);
        verify(em, never()).find(DummyEntity.class, 3L); // short-circuit after failure is fine
    }

    // ---------- toString(BaseEntity) ----------

    @Test
    @DisplayName("toString(entity): returns id; null for null")
    void toString_returnsId_orNull() {
        assertThat(mapper.toString(new DummyEntity(42L))).isEqualTo(42L);
        assertThat(mapper.toString(null)).isNull();
    }
}