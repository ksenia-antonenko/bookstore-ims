package org.example.bookstore.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(UserProps admin, UserProps user) {
    public record UserProps(String username, String password) {}
}