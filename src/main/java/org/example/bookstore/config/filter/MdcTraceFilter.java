package org.example.bookstore.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.example.bookstore.util.MdcKeys;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

public class MdcTraceFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_HEADER = "X-BIMS-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {

        final String traceId = Optional.ofNullable(request.getHeader(TRACE_ID_HEADER))
            .orElse(UUID.randomUUID().toString());
        final String requestInfo =
            request.getMethod() + " " + request.getRequestURI().substring(request.getContextPath().length());

        response.addHeader(TRACE_ID_HEADER, traceId);

        try (final MDC.MDCCloseable traceMdc = MDC.putCloseable(MdcKeys.TRACE_ID, traceId);
             final MDC.MDCCloseable requestMdc = MDC.putCloseable(MdcKeys.REQUEST, requestInfo);
        ) {
            filterChain.doFilter(request, response);
        }
    }
}
