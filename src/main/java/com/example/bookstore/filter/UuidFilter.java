package com.example.bookstore.filter;

import com.example.bookstore.util.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

@Component
@Order(1)
public class UuidFilter extends OncePerRequestFilter {



    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        var uuid = UUID.randomUUID().toString();
        MDC.put(Constants.CORRELATION_ID_HEADER, uuid);
        try {
            var wrappedRequest = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    if (Constants.CORRELATION_ID_HEADER.equalsIgnoreCase(name)) {
                        return uuid;
                    }
                    return super.getHeader(name);
                }

                @Override
                public Enumeration<String> getHeaderNames() {
                    List<String> headerNames = Collections.list(super.getHeaderNames());
                    headerNames.add(Constants.CORRELATION_ID_HEADER);
                    return Collections.enumeration(headerNames);
                }

                @Override
                public Enumeration<String> getHeaders(String name) {
                    if (Constants.CORRELATION_ID_HEADER.equalsIgnoreCase(name)) {
                        return Collections.enumeration(List.of(uuid));
                    }
                    return super.getHeaders(name);
                }
            };
            response.setHeader(Constants.CORRELATION_ID_HEADER, uuid);
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            MDC.remove(Constants.CORRELATION_ID_HEADER);
        }
    }
}