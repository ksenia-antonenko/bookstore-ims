package org.example.bookstore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookstore.auth.BookstoreSecurityProvider;
import org.example.bookstore.auth.CurrentUser;
import org.example.bookstore.auth.CurrentUserImpl;
import org.example.bookstore.auth.PermissionAuthority;
import org.example.bookstore.config.properties.SecurityProperties;
import org.example.bookstore.dto.ApiError;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    private final SecurityProperties securityProperties;
    private final ObjectMapper objectMapper;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                // docs
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // everything else authenticated
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                // 401 when not authenticated
                .authenticationEntryPoint((request, response, authEx) -> writeError(response, 401, "Unauthorized",
                    (authEx.getMessage() != null ? authEx.getMessage() : "Unauthorized"),
                    request.getRequestURI()))
                // 403 when authenticated but not allowed
                .accessDeniedHandler(
                    (request, response, accessDeniedEx) -> writeError(response, 403, "Forbidden", "Access Denied",
                        request.getRequestURI()))
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService users(PasswordEncoder encoder) {
        var admin = User.withUsername(securityProperties.admin().username())
            .password(encoder.encode(securityProperties.admin().password()))
            .authorities(
                new SimpleGrantedAuthority(PermissionAuthority.BASIC_AUTH_AUTHORITY),
                new SimpleGrantedAuthority(PermissionAuthority.MANAGE_ALL)
            ).build();
        var user = User.withUsername(securityProperties.user().username())
            .password(encoder.encode(securityProperties.user().password()))
            .authorities(
                new SimpleGrantedAuthority(PermissionAuthority.BASIC_AUTH_AUTHORITY),
                new SimpleGrantedAuthority(PermissionAuthority.READ_ONLY)
            ).build();
        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public CurrentUser currentUser(BookstoreSecurityProvider securityProvider) {
        return new CurrentUserImpl(securityProvider);
    }


    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    private void writeError(HttpServletResponse response, int status, String error,
                            String message, String path) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        var body = new ApiError(
            ZonedDateTime.now(),
            status,
            error,
            message,
            path,
            List.of()
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
