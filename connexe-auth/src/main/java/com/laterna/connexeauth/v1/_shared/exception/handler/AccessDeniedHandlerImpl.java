package com.laterna.connexeauth.v1._shared.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.laterna.connexeauth.v1._shared.exception.ErrorResponse;
import com.laterna.connexeauth.v1._shared.exception.enumeration.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        ObjectMapper mapper = new ObjectMapper();
        ErrorResponse error = ErrorResponse.builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .type(ErrorType.ACCESS_DENIED)
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        response.getWriter().write(mapper.writeValueAsString(error));
    }
}