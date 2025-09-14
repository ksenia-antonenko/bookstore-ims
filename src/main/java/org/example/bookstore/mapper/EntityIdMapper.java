package org.example.bookstore.mapper;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.bookstore.entity.BaseEntity;
import org.example.bookstore.exception.BookstoreEntityNotFoundException;
import org.mapstruct.TargetType;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class EntityIdMapper {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Allows to resolve an entity id into an entity object.
     *
     * @param id          entity id
     * @param entityClass entity class
     * @param <T>         entity class type
     * @return an entity object
     */
    public <T extends BaseEntity> T resolve(Long id, @TargetType Class<T> entityClass) {
        if (id != null) {
            log.debug("Finding {}: id '{}'", entityClass.getSimpleName(), id);
            return Optional.ofNullable(entityManager.find(entityClass, id))
                .orElseThrow(() -> new BookstoreEntityNotFoundException(id, entityClass));
        }
        return null;
    }

    /**
     * Allows to resolve an entity uuid into an entity object.
     *
     * @param ids          entity ids
     * @param entityClass entity class
     * @param <T>         entity class type
     * @return an entity object
     */
    public <T extends BaseEntity> Set<T> resolveForIds(LinkedHashSet<Long> ids, @TargetType Class<T> entityClass) {
        if (ids != null && !ids.isEmpty()) {
            log.debug("Finding {}: ids '{}'", entityClass.getSimpleName(), ids);
            return ids.stream()
                .map(id -> Optional.ofNullable(entityManager.find(entityClass, id))
                    .orElseThrow(() -> new BookstoreEntityNotFoundException(id, entityClass)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return new LinkedHashSet<>();
    }


    public Long toString(BaseEntity entity) {
        return entity != null ? entity.getId() : null;
    }
}
