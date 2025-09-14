package org.example.bookstore.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Verifies flexible search:
 * - authorNames: partial + case-insensitive; accepts first/last/"first last"
 * - multiple authorNames => OR behavior
 * - genreNames and genreIds filters
 * - combined filters (title + author/genre)
 * - pagination and distinct results.
 *
 */
@Sql(
    scripts = {
        "/testdata/cleanup.sql",
        "/testdata/search-seed.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class BookSearchByRelationsIT extends AbstractIT {

    @Autowired
    MockMvc mvc;

    private static final String USER = "user";
    private static final String PASS = "user123";

    @Test
    @DisplayName("authorNames: case-insensitive partial match on last name (gaiman)")
    void authorNames_partial_caseInsensitive() throws Exception {
        String body = """
            {
              "authorNames": ["GaImAn"],
              "page": 0, "size": 20
            }
            """;
        mvc.perform(post("/api/books/search")
                .with(httpBasic(USER, PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].title",
                containsInAnyOrder("Good Omens", "American Gods", "Neverwhere")));
    }

    @Test
    @DisplayName("authorNames: full 'first last' matches (Terry Pratchett)")
    void authorNames_fullName() throws Exception {
        String body = """
            {
              "authorNames": ["Terry Pratchett"],
              "page": 0, "size": 20
            }
            """;
        mvc.perform(post("/api/books/search")
                .with(httpBasic(USER, PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].title",
                containsInAnyOrder("Good Omens", "Guards! Guards!")));
    }

    @Test
    @DisplayName("authorNames: multiple values => OR across authors")
    void authorNames_multiple_or() throws Exception {
        String body = """
            {
              "authorNames": ["Pratchett","Abercrombie"],
              "page": 0, "size": 20
            }
            """;
        mvc.perform(post("/api/books/search")
                .with(httpBasic(USER, PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].title",
                containsInAnyOrder("Good Omens", "Guards! Guards!", "The Blade Itself")));
    }

    @Test
    @DisplayName("genreNames: match Programming")
    void genreNames_programming() throws Exception {
        String body = """
            {
              "genreNames": ["Programming"],
              "page": 0, "size": 20
            }
            """;
        mvc.perform(post("/api/books/search")
                .with(httpBasic(USER, PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].title",
                containsInAnyOrder("Effective Java", "Refactoring")));
    }

    @Test
    @DisplayName("genreIds: match by id (22 -> Programming)")
    void genreIds_byId() throws Exception {
        String body = """
            {
              "genreIds": [22],
              "page": 0, "size": 20
            }
            """;
        mvc.perform(post("/api/books/search")
                .with(httpBasic(USER, PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].title",
                containsInAnyOrder("Effective Java", "Refactoring")));
    }

    @Test
    @DisplayName("combined: title contains 'good' + authorNames 'gaiman' => only 'Good Omens'")
    void combined_title_and_author() throws Exception {
        String body = """
            {
              "title": "good",
              "authorNames": ["gaiman"],
              "page": 0, "size": 20
            }
            """;
        mvc.perform(post("/api/books/search")
                .with(httpBasic(USER, PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].title").value("Good Omens"));
    }

    @Test
    @DisplayName("Fantasy + price range <= 12.00 returns subset (case-insensitive genre)")
    void genre_and_price_range() throws Exception {
        String body = """
            {
              "genreNames": ["fAnTaSy"],
              "minPrice": 0, "maxPrice": 12.00,
              "page": 0, "size": 20
            }
            """;
        mvc.perform(post("/api/books/search")
                .with(httpBasic(USER, PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].title",
                containsInAnyOrder("Guards! Guards!", "American Gods", "Neverwhere")));
        // "Good Omens" = 12.50 -> excluded
    }

    @Test
    @DisplayName("pagination + sort: Fantasy sorted by title asc, size=2")
    void pagination_and_sort() throws Exception {
        String body = """
            {
              "genreNames": ["Fantasy"],
              "sort": { "orders": [ { "property": "title", "direction": "ASC" } ] },
              "page": 0, "size": 2
            }
            """;
        mvc.perform(post("/api/books/search")
                .with(httpBasic(USER, PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size").value(2))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.content", hasSize(2)));
        // You can add an assertion for first item if your sort is guaranteed stable.
    }

    @Test
    @DisplayName("distinct results when joining many-to-many")
    void distinct_results() throws Exception {
        // authorNames + genreNames together could cause duplicates if DISTINCT isn't applied
        String body = """
            {
              "authorNames": ["gaiman"],
              "genreNames": ["Fantasy"],
              "page": 0, "size": 20
            }
            """;
        mvc.perform(post("/api/books/search")
                .with(httpBasic(USER, PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].title",
                containsInAnyOrder("Good Omens", "American Gods", "Neverwhere")))
            .andExpect(jsonPath("$.content", hasSize(3)));
    }
}
