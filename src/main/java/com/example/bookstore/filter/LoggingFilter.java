package com.example.bookstore.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        var uri = request.getRequestURI();
        if(!uri.startsWith("/auth") || uri.startsWith("/books") || uri.startsWith(("/orders"))) {
            filterChain.doFilter(request, response);
            return;
        }

        var wrappedRequest = new ContentCachingRequestWrapper(request);
        var wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logRequest(wrappedRequest);
            logResponse(wrappedResponse);
            wrappedResponse.copyBodyToResponse(); // Important: copy body back to the original response
        }
    }
    private void logRequest(ContentCachingRequestWrapper request) throws JsonProcessingException {
        var method = request.getMethod();
        var query = request.getQueryString();
        var uri = request.getRequestURI();
        log.info("Incoming Request: {} {}{}", method, uri, (query != null ? "?" + query : ""));
        var requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        var root = objectMapper.readTree(requestBody);
        if(root.has("password")) {
            ((ObjectNode)root).put("password", "****");
        }
        requestBody = objectMapper.writeValueAsString(root);
        log.info("Request Body: {}", requestBody);
        log.info("Request Headers:");
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            log.info("{}: {}", headerName, request.getHeader(headerName));
        });
    }

    private void logResponse(ContentCachingResponseWrapper response) throws IOException {
        log.info("Response Status: {}", response.getStatus());
        var body = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        log.info("Response Body: {}", body);
        logger.info("Response Headers:");
        response.getHeaderNames().forEach(headerName -> {
            log.info("{}: {}", headerName, response.getHeader(headerName));
        });
    }

}
