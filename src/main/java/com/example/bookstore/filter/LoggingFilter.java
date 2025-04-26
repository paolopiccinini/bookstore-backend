package com.example.bookstore.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if(!uri.startsWith("/auth") || uri.startsWith("/books") || uri.startsWith(("/orders"))) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logRequest(wrappedRequest);
            logResponse(wrappedResponse);
            wrappedResponse.copyBodyToResponse(); // Important: copy body back to the original response
        }
    }
    private void logRequest(ContentCachingRequestWrapper request) {
        String method = request.getMethod();
        String query = request.getQueryString();
        String uri = request.getRequestURI();
        log.info("Incoming Request: {} {}{}", method, uri, (query != null ? "?" + query : ""));
        String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        log.info("Request Body: {}", requestBody);
        log.info("Request Headers:");
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            log.info("{}: {}", headerName, request.getHeader(headerName));
        });
    }

    private void logResponse(ContentCachingResponseWrapper response) throws IOException {
        log.info("Response Status: {}", response.getStatus());
        String body = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        log.info("Response Body: {}", body);
        logger.info("Response Headers:");
        response.getHeaderNames().forEach(headerName -> {
            log.info("{}: {}", headerName, response.getHeader(headerName));
        });
    }

}
