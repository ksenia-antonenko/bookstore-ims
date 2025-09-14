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
class BookAuthorControllerIT extends AbstractIT {

    // From search-seed.sql
    private static final long BOOK_GOOD_OMENS = 100L;
    private static final long BOOK_GUARDS = 101L;

    private static final long A_PRATCHETT = 10L;
    private static final long A_GAIMAN = 11L;
    private static final long A_ABERCROMBIE = 12L;

    private static final String ADMIN = "admin";
    private static final String APASS = "admin123";
    private static final String USER = "user";
    private static final String UPASS = "user123";

    private int countLinks(long bookId) {
        return jdbc.queryForObject("select count(*) from book_author where book_id=?", Integer.class, bookId);
    }

    private int hasLink(long bookId, long authorId) {
        return jdbc.queryForObject("select count(*) from book_author where book_id=? and author_id=?",
            Integer.class, bookId, authorId);
    }

    @Test
    @DisplayName("401 when unauthenticated")
    void unauthenticated_401() throws Exception {
        mvc.perform(put("/api/books/{bookId}/authors", BOOK_GOOD_OMENS)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[10,11]}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("403 for READ_ONLY user")
    void read_only_403() throws Exception {
        mvc.perform(put("/api/books/{bookId}/authors", BOOK_GOOD_OMENS)
                .with(httpBasic(USER, UPASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[10,11]}"))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("replaceAuthors: set exactly the provided authors")
    void replace_authors() throws Exception {
        // Good Omens initially has (10,11) per seed — replace with single 12
        mvc.perform(put("/api/books/{bookId}/authors", BOOK_GOOD_OMENS)
                .with(httpBasic(ADMIN, APASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[12]}"))
            .andExpect(status().isNoContent());

        assertThat(countLinks(BOOK_GOOD_OMENS)).isEqualTo(1);
        assertThat(hasLink(BOOK_GOOD_OMENS, A_ABERCROMBIE)).isEqualTo(1);
        assertThat(hasLink(BOOK_GOOD_OMENS, A_PRATCHETT)).isZero();
        assertThat(hasLink(BOOK_GOOD_OMENS, A_GAIMAN)).isZero();

        // Replace again with (10,11)
        mvc.perform(put("/api/books/{bookId}/authors", BOOK_GOOD_OMENS)
                .with(httpBasic(ADMIN, APASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[10,11]}"))
            .andExpect(status().isNoContent());

        assertThat(countLinks(BOOK_GOOD_OMENS)).isEqualTo(2);
        assertThat(hasLink(BOOK_GOOD_OMENS, A_PRATCHETT)).isEqualTo(1);
        assertThat(hasLink(BOOK_GOOD_OMENS, A_GAIMAN)).isEqualTo(1);
    }

    @Test
    @DisplayName("addAuthors: add new, ignore duplicates")
    void add_authors() throws Exception {
        // Guards! Guards! initially has (10). Add (10,11) -> expect {10,11}
        mvc.perform(post("/api/books/{bookId}/authors", BOOK_GUARDS)
                .with(httpBasic(ADMIN, APASS))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[10,11]}"))
            .andExpect(status().isNoContent());

        assertThat(countLinks(BOOK_GUARDS)).isEqualTo(2);
        assertThat(hasLink(BOOK_GUARDS, A_PRATCHETT)).isEqualTo(1);
        assertThat(hasLink(BOOK_GUARDS, A_GAIMAN)).isEqualTo(1);
    }

    @Test
    @DisplayName("removeAuthor: remove only specified link")
    void remove_author() throws Exception {
        // Good Omens has (10,11). Remove 10 → remains 11
        mvc.perform(delete("/api/books/{bookId}/authors/{authorId}", BOOK_GOOD_OMENS, A_PRATCHETT)
                .with(httpBasic(ADMIN, APASS)))
            .andExpect(status().isNoContent());

        assertThat(hasLink(BOOK_GOOD_OMENS, A_PRATCHETT)).isZero();
        assertThat(hasLink(BOOK_GOOD_OMENS, A_GAIMAN)).isEqualTo(1);
    }
}
