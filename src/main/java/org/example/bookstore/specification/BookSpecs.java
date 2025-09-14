package org.example.bookstore.specification;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import org.example.bookstore.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public final class BookSpecs {

    private BookSpecs() {
    }

    public static Specification<Book> titleContains(String q) {
        return (root, cq, cb) ->
            isBlank(q) ? null : cb.like(cb.lower(root.get("title")), "%" + q.toLowerCase() + "%");
    }

    public static Specification<Book> priceBetween(BigDecimal min, BigDecimal max) {
        return (r, cq, cb) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min != null && max != null) {
                return cb.between(r.get("price"), min, max);
            }
            return min != null ? cb.greaterThanOrEqualTo(r.get("price"), min)
                : cb.lessThanOrEqualTo(r.get("price"), max);
        };
    }

    public static Specification<Book> quantityBetween(Integer min, Integer max) {
        return (r, cq, cb) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min != null && max != null) {
                return cb.between(r.get("quantity"), min, max);
            }
            return min != null ? cb.greaterThanOrEqualTo(r.get("quantity"), min)
                : cb.lessThanOrEqualTo(r.get("quantity"), max);
        };
    }

    public static Specification<Book> ratingBetween(BigDecimal min, BigDecimal max) {
        return (r, cq, cb) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min != null && max != null) {
                return cb.between(r.get("rating"), min, max);
            }
            return min != null ? cb.greaterThanOrEqualTo(r.get("rating"), min)
                : cb.lessThanOrEqualTo(r.get("rating"), max);
        };
    }

    public static Specification<Book> createdBetween(ZonedDateTime from, ZonedDateTime to) {
        return (r, cq, cb) -> {
            if (from == null && to == null) {
                return null;
            }
            if (from != null && to != null) {
                return cb.between(r.get("createdAt"), from, to);
            }
            return from != null ? cb.greaterThanOrEqualTo(r.get("createdAt"), from)
                : cb.lessThanOrEqualTo(r.get("createdAt"), to);
        };
    }

    public static Specification<Book> updatedBetween(ZonedDateTime from, ZonedDateTime to) {
        return (r, cq, cb) -> {
            if (from == null && to == null) {
                return null;
            }
            if (from != null && to != null) {
                return cb.between(r.get("updatedAt"), from, to);
            }
            return from != null ? cb.greaterThanOrEqualTo(r.get("updatedAt"), from)
                : cb.lessThanOrEqualTo(r.get("updatedAt"), to);
        };
    }

    // --- Authors (many-to-many) ---
    public static Specification<Book> authorIdsAny(Collection<Long> ids) {
        return (root, cq, cb) -> {
            if (ids == null || ids.isEmpty()) {
                return null;
            }
            var authors = root.joinSet("authors");
            cq.distinct(true);
            return authors.get("id").in(ids);
        };
    }

    public static Specification<Book> authorNamesAny(Collection<String> names) {
        return (root, cq, cb) -> {
            if (names == null || names.isEmpty()) {
                return null;
            }
            var a = root.joinSet("authors");
            cq.distinct(true);
            // match first, last, or "first last"
            var or = cb.disjunction();
            for (String n : names) {
                if (isBlank(n)) {
                    continue;
                }
                String like = "%" + n.toLowerCase() + "%";
                or = cb.or(or,
                    cb.like(cb.lower(a.get("firstName")), like),
                    cb.like(cb.lower(a.get("lastName")), like),
                    cb.like(cb.lower(cb.concat(cb.concat(a.get("firstName"), " "), a.get("lastName"))), like)
                );
            }
            return or;
        };
    }

    // --- Genres (many-to-many) ---
    public static Specification<Book> genreIdsAny(Collection<Long> ids) {
        return (root, cq, cb) -> {
            if (ids == null || ids.isEmpty()) {
                return null;
            }
            var g = root.joinSet("genres");
            cq.distinct(true);
            return g.get("id").in(ids);
        };
    }

    public static Specification<Book> genreNamesAny(Collection<String> names) {
        return (root, cq, cb) -> {
            if (names == null || names.isEmpty()) {
                return null;
            }
            var g = root.joinSet("genres");
            cq.distinct(true);
            var or = cb.disjunction();
            for (String n : names) {
                if (isBlank(n)) {
                    continue;
                }
                String like = "%" + n.toLowerCase() + "%";
                or = cb.or(or, cb.like(cb.lower(g.get("name")), like));
            }
            return or;
        };
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
