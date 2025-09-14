package org.example.bookstore.controller;

import org.example.bookstore.SharedPostgresqlContainer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Testcontainers
@TestPropertySource(properties = {
    "spring.test.database.replace=NONE",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.liquibase.change-log=classpath:db/changesets/db.changelog.xml",
    "spring.sql.init.mode=never"
})
public abstract class AbstractIT {

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected JdbcTemplate jdbc;

    @Container
    private static final SharedPostgresqlContainer POSTGRESQL_CONTAINER = SharedPostgresqlContainer.getInstance();

}