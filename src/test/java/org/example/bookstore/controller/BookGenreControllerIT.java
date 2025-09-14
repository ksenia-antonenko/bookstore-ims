package org.example.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@Sql(
    scripts = {
        "/testdata/cleanup.sql",
        "/testdata/search-seed.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class BookGenreControllerIT extends AbstractIT {

    // From search-seed.sql
    private static final long BOOK_EFFECTIVE_JAVA = 103L; // Programming
    private static final long BOOK_REFACTORING = 104L; // Programming
    private static final long BOOK_GOOD_OMENS = 100L; // Fantasy

    private static final long G_FANTASY = 20L;
    private static final long G_SCIFI = 21L;
    private static final long G_PROGRAMMING = 22L;

    private static final String ADMIN = "admin";
    private static final String APASS = "admin123";
    private static final String USER = "user";
    private static final String UPASS = "user123";

    private int countLinks(long bookId) {
        return jdbc.queryForObject("select count(*) from book_genre where book_id=?", Integer.class, bookId);
    }

    private int hasLink(long bookId, long genreId) {
        return jdbc.queryForObject("select count(*) from book_genre where book_id=? and genre_id=?",
            Integer.class, bookId, genreId);
    }

    @Test
    @DisplayName("401 when unauthenticated")
    void unauthenticated_401() throws Exception {
        mvc.perform(put("/api/books/{bookId}/genres", BOOK_GOOD_OMENS)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[20,21]}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("403 for READ_ONLY user")
    void read_only_403() throws Exception {
        mvc.perform(put("/api/books/{bookId}/genres", BOOK_GOOD_OMENS)
                .with(httpBasic(USER, UPASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[20,21]}"))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("replaceGenres: overwrite with exact set")
    void replace_genres() throws Exception {
        // Good Omens initially Fantasy (20); replace with Sci-Fi (21)
        mvc.perform(put("/api/books/{bookId}/genres", BOOK_GOOD_OMENS)
                .with(httpBasic(ADMIN, APASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[21]}"))
            .andExpect(status().isNoContent());

        assertThat(countLinks(BOOK_GOOD_OMENS)).isEqualTo(1);
        assertThat(hasLink(BOOK_GOOD_OMENS, G_SCIFI)).isEqualTo(1);
        assertThat(hasLink(BOOK_GOOD_OMENS, G_FANTASY)).isZero();

        // Replace back to Fantasy only
        mvc.perform(put("/api/books/{bookId}/genres", BOOK_GOOD_OMENS)
                .with(httpBasic(ADMIN, APASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[20]}"))
            .andExpect(status().isNoContent());

        assertThat(countLinks(BOOK_GOOD_OMENS)).isEqualTo(1);
        assertThat(hasLink(BOOK_GOOD_OMENS, G_FANTASY)).isEqualTo(1);
    }

    @Test
    @DisplayName("addGenres: add new; ignore duplicates")
    void add_genres() throws Exception {
        // Effective Java initially Programming; add Fantasy -> {Programming, Fantasy}
        mvc.perform(post("/api/books/{bookId}/genres", BOOK_EFFECTIVE_JAVA)
                .with(httpBasic(ADMIN, APASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[20,22]}")) // 22 already present
            .andExpect(status().isNoContent());

        assertThat(countLinks(BOOK_EFFECTIVE_JAVA)).isEqualTo(2);
        assertThat(hasLink(BOOK_EFFECTIVE_JAVA, G_PROGRAMMING)).isEqualTo(1);
        assertThat(hasLink(BOOK_EFFECTIVE_JAVA, G_FANTASY)).isEqualTo(1);
    }

    @Test
    @DisplayName("removeGenre: remove only specified link")
    void remove_genre() throws Exception {
        // Refactoring initially Programming; add Fantasy to have 2, then remove one
        mvc.perform(post("/api/books/{bookId}/genres", BOOK_REFACTORING)
                .with(httpBasic(ADMIN, APASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[20]}"))
            .andExpect(status().isNoContent());

        assertThat(countLinks(BOOK_REFACTORING)).isEqualTo(2);

        mvc.perform(delete("/api/books/{bookId}/genres/{genreId}", BOOK_REFACTORING, G_PROGRAMMING)
                .with(httpBasic(ADMIN, APASS)))
            .andExpect(status().isNoContent());

        assertThat(hasLink(BOOK_REFACTORING, G_PROGRAMMING)).isZero();
        assertThat(hasLink(BOOK_REFACTORING, G_FANTASY)).isEqualTo(1);
    }
}
