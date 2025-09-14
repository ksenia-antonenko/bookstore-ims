package org.example.bookstore.controller;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class BookControllerIT extends AbstractIT {

    @Nested
    class BookCrud {
        @Test
        @DisplayName("ADMIN: create → get → patch → delete")
        void admin_crud_flow() throws Exception {
            // CREATE
            String create = """
                {"title":"Clean Architecture","price":33.00,"quantity":5,"rating":5}
                """;
            var created = mvc.perform(post("/api/books")
                    .with(httpBasic("admin", "admin123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(create))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Clean Architecture"))
                .andExpect(jsonPath("$.price").value(33.00))
                .andReturn();

            String json = created.getResponse().getContentAsString();
            long id = ((Number) com.jayway.jsonpath.JsonPath.read(json, "$.id")).longValue();

            // GET
            mvc.perform(get("/api/books/{id}", id).with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Clean Architecture"));

            // PATCH — send only fields you want to change (JsonNullable on DTO)
            String patch = """
                {"price": 35.50, "quantity": 7}
                """;
            mvc.perform(patch("/api/books/{id}", id)
                    .with(httpBasic("admin", "admin123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(patch))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.quantity").value(7));

            // DELETE
            mvc.perform(delete("/api/books/{id}", id).with(httpBasic("admin", "admin123")))
                .andExpect(status().isNoContent());

            // GET after delete → 404
            mvc.perform(get("/api/books/{id}", id).with(httpBasic("admin", "admin123")))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Create validation: missing title → 400")
        void create_validation_400() throws Exception {
            String bad = """
                {"price": 9.99, "quantity": 1, "rating": 5}
                """;
            mvc.perform(post("/api/books")
                    .with(httpBasic("admin", "admin123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(bad))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Create validation: negative price → 400")
        void create_negative_price_400() throws Exception {
            String bad = """
                {"title":"Bad","price":-1,"quantity":1,"rating":5}
                """;
            mvc.perform(post("/api/books")
                    .with(httpBasic("admin", "admin123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(bad))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class BookSearch {
        @Test
        @DisplayName("Null body → controller builds default BookSearchRequestDto")
        void null_body_defaults() throws Exception {
            mvc.perform(post("/api/books/search").with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.content", isA(Iterable.class)));
        }

        @Test
        @DisplayName("Filters + sort and paging")
        void filters_sort_paging() throws Exception {
            String body = """
                {
                  "title": "clean",
                  "minPrice": 0,
                  "maxPrice": 999,
                  "minRating": 0,
                  "maxRating": 5,
                  "sort": { "orders": [ { "property": "title", "direction": "ASC" } ] },
                  "page": 0,
                  "size": 5
                }
                """;
            mvc.perform(post("/api/books/search")
                    .with(httpBasic("user", "user123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.content", isA(Iterable.class)));
        }
    }

}
