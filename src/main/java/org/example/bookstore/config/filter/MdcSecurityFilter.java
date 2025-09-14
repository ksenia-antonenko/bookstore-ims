package org.example.bookstore.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.bookstore.auth.BookstoreSecurityProvider;
import org.example.bookstore.auth.CurrentUser;
import org.example.bookstore.domain.user.User;
import org.example.bookstore.util.MdcKeys;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class MdcSecurityFilter extends OncePerRequestFilter {

    private final BookstoreSecurityProvider securityProvider;
    private final CurrentUser currentUser;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String username;
        if (currentUser.isBasicAuthUsed()) {
            username = securityProvider.getBasicUsername()
                .orElse(null);
        } else {
            username = securityProvider.getUserDetails()
                .map(User::getUsername)
                .orElse(null);
        }

        try (final MDC.MDCCloseable closeable = MDC.putCloseable(MdcKeys.USERNAME, username)) {
            filterChain.doFilter(request, response);
        }
    }
}
