package org.example.bookstore.config;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import lombok.extern.slf4j.Slf4j;
import org.example.bookstore.auth.BookstoreSecurityProvider;
import org.example.bookstore.auth.CurrentUser;
import org.example.bookstore.config.filter.MdcSecurityFilter;
import org.example.bookstore.config.filter.MdcTraceFilter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LoggingConfig {

    /**
     * Registers {@link MdcSecurityFilter} after Spring security chain.
     */
    @Bean
    public FilterRegistrationBean<MdcSecurityFilter> mdcSecurityFilter(
        BookstoreSecurityProvider securityProvider,
        CurrentUser currentUser,
        SecurityProperties securityProperties
    ) {
        final FilterRegistrationBean<MdcSecurityFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        final MdcSecurityFilter filter = new MdcSecurityFilter(securityProvider, currentUser);

        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setOrder(securityProperties.getFilter().getOrder() + 1);
        logFilterRegistrationBean(filterRegistrationBean);

        return filterRegistrationBean;
    }

    /**
     * Registers {@link MdcTraceFilter} before Spring security chain.
     */
    @Bean
    public FilterRegistrationBean<MdcTraceFilter> mdcTraceFilter() {
        final FilterRegistrationBean<MdcTraceFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        final MdcTraceFilter filter = new MdcTraceFilter();

        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setOrder(HIGHEST_PRECEDENCE);
        logFilterRegistrationBean(filterRegistrationBean);

        return filterRegistrationBean;
    }

    private void logFilterRegistrationBean(FilterRegistrationBean<?> filterRegistrationBean) {
        log.debug("Filter registration for '{}': order - '{}', url patterns - '{}'",
            filterRegistrationBean.getFilter().getClass().getSimpleName(), filterRegistrationBean.getOrder(),
            filterRegistrationBean.getUrlPatterns());
    }
}
