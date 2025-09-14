package org.example.bookstore;

import org.testcontainers.containers.PostgreSQLContainer;

public class SharedPostgresqlContainer extends PostgreSQLContainer<SharedPostgresqlContainer> {

    private static final String IMAGE = "postgres:15.4";
    private static SharedPostgresqlContainer container;

    private SharedPostgresqlContainer() {
        super(IMAGE);
    }

    /**
     * Creates a singleton instance if no one exits.
     *
     * @return singleton container instance
     */
    public static synchronized SharedPostgresqlContainer getInstance() {
        if (container == null) {
            container = new SharedPostgresqlContainer();
            // If you need a fixed port to look inside, uncomment next line
            // container.addFixedExposedPort(2345, 5432);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("spring.datasource.url", container.getJdbcUrl());
        System.setProperty("spring.datasource.username", container.getUsername());
        System.setProperty("spring.datasource.password", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
