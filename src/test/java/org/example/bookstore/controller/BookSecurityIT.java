package org.example.bookstore.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

class BookSecurityIT extends AbstractIT {

    @Test
    void noAuth_is401() throws Exception {
        mvc.perform(get("/api/books/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void user_forbidden_on_write() throws Exception {
        mvc.perform(delete("/api/books/1").with(httpBasic("user", "user123")))
            .andExpect(status().isForbidden());
    }

    @Test
    void user_can_search() throws Exception {
        mvc.perform(post("/api/books/search").with(httpBasic("user", "user123")))
            .andExpect(status().isOk());
    }
}
